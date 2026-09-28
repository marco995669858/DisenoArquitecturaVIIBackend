package com.dataverse.rentals.service.impl;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataverse.rentals.mapper.MapperContrato;
import com.dataverse.rentals.mapper.MapperParametroSistema;
import com.dataverse.rentals.mapper.MapperUnidad;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.contrato.request.ContratoRequest;
import com.dataverse.rentals.model.contrato.request.RenovarContratoRequest;
import com.dataverse.rentals.model.contrato.response.AlertaVencimientoResultDTO;
import com.dataverse.rentals.model.contrato.response.ContratoDTO;
import com.dataverse.rentals.model.contrato.response.ContratoPorVencerDTO;
import com.dataverse.rentals.model.contrato.response.RenovarContratoResultDTO;
import com.dataverse.rentals.security.AuditContext;
import com.dataverse.rentals.util.ErrorMessages;
import com.dataverse.rentals.service.ServiceContrato;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplContrato implements ServiceContrato {

    private static final int DIAS_ALERTA_POR_DEFECTO = 30;

    private final MapperContrato mapper;
    private final MapperParametroSistema mapperParametro;
    private final MapperUnidad mapperUnidad;
    private final AuditContext audit;

    @Override
    public ResponseDTO<List<ContratoDTO>> listarContratos() {
        try {
            List<ContratoDTO> data = mapper.listarContratos();
            if (data.isEmpty()) {
                return new ResponseDTO<>(null, false, "No se encontraron registros.");
            }
            return new ResponseDTO<>(data, false, "Se encontraron registros.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    /**
     * unidad.estado nunca se toca a mano para OCUPADO: lo pone y lo quita el
     * sistema segun exista o no un contrato VIGENTE, para que el campo no
     * pueda quedar diciendo algo que ningun contrato respalda. DISPONIBLE y
     * MANTENIMIENTO si se siguen editando desde la propia unidad (RF-01).
     */
    @Override
    @Transactional
    public ResponseDTO<Integer> insertUpdateContrato(ContratoRequest args) {
        try {
            Integer idUsuario = audit.usuarioActual();
            String ip = audit.ipActual();
            boolean esNuevo = args.getIdContrato() == null;
            Integer idUnidadAnterior = esNuevo ? null : mapper.obtenerIdUnidadDeContrato(args.getIdContrato());

            if (esNuevo) {
                mapper.insertarContrato(args, idUsuario, ip);
            } else {
                mapper.actualizarContrato(args, idUsuario, ip);
            }

            if ("VIGENTE".equals(args.getEstado())) {
                mapperUnidad.marcarUnidadOcupada(args.getIdUnidad(), idUsuario, ip);
                if (idUnidadAnterior != null && !idUnidadAnterior.equals(args.getIdUnidad())) {
                    mapperUnidad.marcarUnidadDisponible(idUnidadAnterior, idUsuario, ip);
                }
            } else {
                mapperUnidad.marcarUnidadDisponible(args.getIdUnidad(), idUsuario, ip);
                if (idUnidadAnterior != null && !idUnidadAnterior.equals(args.getIdUnidad())) {
                    mapperUnidad.marcarUnidadDisponible(idUnidadAnterior, idUsuario, ip);
                }
            }

            String mensaje = esNuevo ? "Contrato registrado correctamente." : "Contrato actualizado correctamente.";
            return new ResponseDTO<>(args.getIdContrato(), false, mensaje);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    @Transactional
    public ResponseDTO<Integer> eliminarContrato(ContratoRequest args) {
        try {
            if (mapper.contarLiquidacionesDeContrato(args.getIdContrato()) > 0) {
                return new ResponseDTO<>(true, "No se puede eliminar el contrato: tiene liquidaciones asociadas.");
            }

            Integer idUnidad = mapper.obtenerIdUnidadDeContrato(args.getIdContrato());
            Integer idUsuario = audit.usuarioActual();
            String ip = audit.ipActual();

            int filas = mapper.eliminarContrato(args.getIdContrato(), idUsuario, ip);
            if (filas <= 0) {
                return new ResponseDTO<>(true, "No se encontró el contrato a eliminar.");
            }
            if (idUnidad != null) {
                mapperUnidad.marcarUnidadDisponible(idUnidad, idUsuario, ip);
            }
            return new ResponseDTO<>(filas, false, "Contrato eliminado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<AlertaVencimientoResultDTO> verificarVencimientos() {
        return verificarVencimientos(audit.usuarioActual(), audit.ipActual());
    }

    /**
     * CU-03b: a diferencia de editar el contrato existente, renovar deja
     * intacto el historial -- el contrato viejo pasa a RENOVADO (nunca se
     * sobreescribe ni se borra) y se crea uno nuevo VIGENTE que retoma desde
     * la fecha_fin anterior. Solo tiene sentido sobre un contrato que sigue
     * activo para la unidad (VIGENTE) o que acaba de vencer sin haberse
     * cerrado todavia (VENCIDO); uno ya RENOVADO o FINALIZADO no se reabre.
     */
    @Override
    @Transactional
    public ResponseDTO<RenovarContratoResultDTO> renovarContrato(RenovarContratoRequest args) {
        try {
            ContratoDTO contratoActual = mapper.obtenerContratoPorId(args.getIdContrato());
            if (contratoActual == null) {
                return new ResponseDTO<>(true, "No se encontró el contrato a renovar.");
            }
            if (!"VIGENTE".equals(contratoActual.getEstado()) && !"VENCIDO".equals(contratoActual.getEstado())) {
                return new ResponseDTO<>(true, "Solo se puede renovar un contrato VIGENTE o VENCIDO.");
            }
            if (args.getNuevaFechaFin() == null || !args.getNuevaFechaFin().isAfter(contratoActual.getFechaFin())) {
                return new ResponseDTO<>(true, "La nueva fecha de fin debe ser posterior a la fecha de fin actual (" + contratoActual.getFechaFin() + ").");
            }

            Integer idUsuario = audit.usuarioActual();
            String ip = audit.ipActual();

            mapper.marcarContratoRenovado(contratoActual.getIdContrato(), idUsuario, ip);

            ContratoRequest nuevoContrato = new ContratoRequest();
            nuevoContrato.setIdUnidad(contratoActual.getIdUnidad());
            nuevoContrato.setIdInquilino(contratoActual.getIdInquilino());
            nuevoContrato.setFechaInicio(contratoActual.getFechaFin());
            nuevoContrato.setFechaFin(args.getNuevaFechaFin());
            nuevoContrato.setMontoAlquiler(args.getNuevoMontoAlquiler() != null ? args.getNuevoMontoAlquiler() : contratoActual.getMontoAlquiler());
            nuevoContrato.setRutaContratoDigital(args.getNuevaRutaContratoDigital() != null ? args.getNuevaRutaContratoDigital() : contratoActual.getRutaContratoDigital());
            nuevoContrato.setEstado("VIGENTE");

            mapper.insertarContrato(nuevoContrato, idUsuario, ip);
            mapperUnidad.marcarUnidadOcupada(contratoActual.getIdUnidad(), idUsuario, ip);

            RenovarContratoResultDTO resultado = new RenovarContratoResultDTO(
                    contratoActual.getIdContrato(), nuevoContrato.getIdContrato(),
                    contratoActual.getNumeroUnidad(), contratoActual.getNombreInquilino(), args.getNuevaFechaFin());

            return new ResponseDTO<>(resultado, false, "Contrato renovado hasta " + args.getNuevaFechaFin() + ".");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    /**
     * Tarea programada de RN-03: corre sola todos los dias a las 6:00 a.m.,
     * sin depender de que alguien abra el dashboard para que los contratos
     * vencidos se actualicen. AuditContext no se puede usar aca -- lee el
     * HttpServletRequest de la peticion actual, y una tarea programada no
     * corre dentro de ninguna peticion -- por eso se registra sin usuario
     * (columna nullable) y con un IP fijo que identifica el origen.
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void tareaProgramadaVerificarVencimientos() {
        ResponseDTO<AlertaVencimientoResultDTO> resultado = verificarVencimientos(null, "sistema (tarea programada)");
        if (resultado.isError()) {
            log.error("Fallo la verificacion programada de vencimientos de contrato: {}", resultado.getMensaje());
        } else {
            log.info("Verificacion programada de vencimientos de contrato: {}", resultado.getMensaje());
        }
    }

    private ResponseDTO<AlertaVencimientoResultDTO> verificarVencimientos(Integer idUsuario, String ip) {
        try {
            // Se libera la unidad ANTES de marcar el contrato VENCIDO: esta
            // consulta identifica las unidades OCUPADO por un contrato que
            // esta por vencer usando su estado VIGENTE todavia vigente en
            // ese instante (ver MapperContrato.liberarUnidadesDeContratosPorVencer).
            mapper.liberarUnidadesDeContratosPorVencer(idUsuario, ip);
            int marcadosVencidos = mapper.marcarContratosVencidos(idUsuario, ip);

            int dias = DIAS_ALERTA_POR_DEFECTO;
            String diasTexto = mapperParametro.obtenerValorPorClave("DIAS_ALERTA_VENCIMIENTO");
            if (diasTexto != null) {
                try {
                    dias = Integer.parseInt(diasTexto.trim());
                } catch (NumberFormatException ignored) {
                    // valor mal configurado en parametro_sistema: se usa el default de 30 dias
                }
            }

            List<ContratoPorVencerDTO> porVencer = mapper.listarContratosPorVencer(dias);
            AlertaVencimientoResultDTO resultado = new AlertaVencimientoResultDTO(marcadosVencidos, porVencer);

            String mensaje = marcadosVencidos + " contrato(s) marcado(s) como VENCIDO. "
                    + porVencer.size() + " contrato(s) vencen en los proximos " + dias + " dias.";
            return new ResponseDTO<>(resultado, false, mensaje);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }
}
