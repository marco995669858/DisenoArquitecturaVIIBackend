package com.dataverse.rentals.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataverse.rentals.mapper.MapperDetalleLiquidacion;
import com.dataverse.rentals.mapper.MapperLiquidacion;
import com.dataverse.rentals.mapper.MapperParametroSistema;
import com.dataverse.rentals.mapper.MapperPeriodoFacturacion;
import com.dataverse.rentals.mapper.MapperProrrateo;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.detalleLiquidacion.request.DetalleLiquidacionRequest;
import com.dataverse.rentals.model.liquidacion.request.GenerarLiquidacionRequest;
import com.dataverse.rentals.model.liquidacion.request.LiquidacionRequest;
import com.dataverse.rentals.model.liquidacion.response.ContratoVigenteDTO;
import com.dataverse.rentals.model.liquidacion.response.GenerarLiquidacionResultDTO;
import com.dataverse.rentals.model.liquidacion.response.LiquidacionDTO;
import com.dataverse.rentals.model.liquidacion.response.LiquidacionGeneradaDTO;
import com.dataverse.rentals.model.liquidacion.response.MantenimientoImputableDTO;
import com.dataverse.rentals.model.prorrateo.request.CalcularProrrateoRequest;
import com.dataverse.rentals.security.AuditContext;
import com.dataverse.rentals.util.ErrorMessages;
import com.dataverse.rentals.service.ServiceLiquidacion;
import com.dataverse.rentals.service.ServiceProrrateo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplLiquidacion implements ServiceLiquidacion {

    private final MapperLiquidacion mapper;
    private final MapperDetalleLiquidacion mapperDetalle;
    private final MapperProrrateo mapperProrrateo;
    private final MapperParametroSistema mapperParametro;
    private final MapperPeriodoFacturacion mapperPeriodo;
    private final ServiceProrrateo serviceProrrateo;
    private final AuditContext audit;

    @Override
    public ResponseDTO<List<LiquidacionDTO>> listarLiquidaciones() {
        try {
            List<LiquidacionDTO> data = mapper.listarLiquidaciones();
            if (data.isEmpty()) {
                return new ResponseDTO<>(null, false, "No se encontraron registros.");
            }
            return new ResponseDTO<>(data, false, "Se encontraron registros.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> insertUpdateLiquidacion(LiquidacionRequest args) {
        try {
            if (args.getIdLiquidacion() == null) {
                Integer idUsuario = audit.usuarioActual();
                mapperPeriodo.asegurarPeriodo(args.getPeriodo(), idUsuario);
                mapper.insertarLiquidacion(args, idUsuario, audit.ipActual());
                return new ResponseDTO<>(args.getIdLiquidacion(), false, "Liquidación registrada correctamente.");
            }
            mapper.actualizarLiquidacion(args, audit.usuarioActual(), audit.ipActual());
            return new ResponseDTO<>(args.getIdLiquidacion(), false, "Liquidación actualizada correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> eliminarLiquidacion(LiquidacionRequest args) {
        try {
            if (mapper.contarPagosDeLiquidacion(args.getIdLiquidacion()) > 0) {
                return new ResponseDTO<>(true, "No se puede eliminar la liquidación: tiene pagos registrados.");
            }
            if (mapper.contarDetallesDeLiquidacion(args.getIdLiquidacion()) > 0) {
                return new ResponseDTO<>(true, "No se puede eliminar la liquidación: tiene conceptos adicionales asociados.");
            }

            int filas = mapper.eliminarLiquidacion(args.getIdLiquidacion(), audit.usuarioActual(), audit.ipActual());
            if (filas <= 0) {
                return new ResponseDTO<>(true, "No se encontró la liquidación a eliminar.");
            }
            return new ResponseDTO<>(filas, false, "Liquidación eliminada correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    /**
     * CU-06 -- Generar Liquidacion. Simplificacion respecto del analisis: el
     * flujo original separa "mostrar liquidacion preliminar" (paso 5) de
     * "confirmar" (paso 6); aca se emite directo en un solo paso, sin una
     * vista previa editable -- consistente con como se implemento tambien
     * CU-05 (sin un estado intermedio de revision).
     */
    @Override
    @Transactional
    public ResponseDTO<GenerarLiquidacionResultDTO> generarLiquidaciones(GenerarLiquidacionRequest args) {
        try {
            String periodo = args.getPeriodo();
            if (periodo == null || periodo.isBlank()) {
                return new ResponseDTO<>(true, "Debe indicar el periodo a liquidar.");
            }

            Integer idUsuario = audit.usuarioActual();
            String ip = audit.ipActual();

            List<String> advertencias = new ArrayList<>();
            List<String> excepciones = new ArrayList<>();
            List<String> omitidas = new ArrayList<>();
            List<LiquidacionGeneradaDTO> generadas = new ArrayList<>();

            // Paso 2 (CU-06 incluye CU-05): calcular el prorrateo de LUZ y AGUA
            // para todo el periodo antes de componer las liquidaciones. Si un
            // servicio no se puede calcular (faltan lecturas o el recibo), se
            // continua sin ese concepto en vez de bloquear todas las unidades;
            // se dejar constancia en "advertencias" para que el administrador
            // sepa por que una liquidacion no trae luz o agua.
            for (String servicio : new String[]{"LUZ", "AGUA"}) {
                CalcularProrrateoRequest calc = new CalcularProrrateoRequest();
                calc.setPeriodo(periodo);
                calc.setTipoServicio(servicio);
                ResponseDTO<?> resultadoCalculo = serviceProrrateo.calcularProrrateo(calc);
                if (resultadoCalculo.isError()) {
                    advertencias.add("No se calculo el prorrateo de " + servicio + ": " + resultadoCalculo.getMensaje());
                }
            }

            String diasVencimientoTexto = mapperParametro.obtenerValorPorClave("DIAS_VENCIMIENTO_LIQUIDACION");
            long diasVencimiento = 10;
            if (diasVencimientoTexto != null) {
                try {
                    diasVencimiento = Long.parseLong(diasVencimientoTexto.trim());
                } catch (NumberFormatException ignored) {
                    // valor mal configurado: se usa el default de 10 dias
                }
            }
            LocalDate fechaEmision = LocalDate.now();
            LocalDate fechaVencimiento = fechaEmision.plusDays(diasVencimiento);

            List<ContratoVigenteDTO> contratosVigentes = mapper.listarContratosVigentes();

            for (ContratoVigenteDTO cv : contratosVigentes) {
                // RN-04 / FA-02: no duplicar liquidacion para el mismo contrato y periodo.
                if (mapper.buscarLiquidacionExistente(cv.getIdContrato(), periodo) != null) {
                    omitidas.add("Unidad " + cv.getNumeroUnidad() + ": ya existe una liquidacion para " + periodo + " (RN-04).");
                    continue;
                }

                BigDecimal montoLuz = mapperProrrateo.obtenerMontoCalculado(cv.getIdUnidad(), periodo, "LUZ");
                BigDecimal montoAgua = mapperProrrateo.obtenerMontoCalculado(cv.getIdUnidad(), periodo, "AGUA");
                Integer idProrrateoLuz = mapperProrrateo.buscarIdProrrateoExistente(cv.getIdUnidad(), periodo, "LUZ");
                Integer idProrrateoAgua = mapperProrrateo.buscarIdProrrateoExistente(cv.getIdUnidad(), periodo, "AGUA");
                List<MantenimientoImputableDTO> mantenimientos = mapper.listarMantenimientoImputablePendiente(cv.getIdUnidad(), periodo);

                BigDecimal montoOtros = BigDecimal.ZERO;
                for (MantenimientoImputableDTO m : mantenimientos) {
                    montoOtros = montoOtros.add(m.getMonto());
                }

                LiquidacionRequest liquidacion = new LiquidacionRequest();
                liquidacion.setIdContrato(cv.getIdContrato());
                liquidacion.setPeriodo(periodo);
                liquidacion.setMontoAlquiler(cv.getMontoAlquiler());
                liquidacion.setMontoLuz(montoLuz != null ? montoLuz : BigDecimal.ZERO);
                liquidacion.setMontoAgua(montoAgua != null ? montoAgua : BigDecimal.ZERO);
                liquidacion.setMontoOtros(montoOtros);
                liquidacion.setFechaEmision(fechaEmision);
                liquidacion.setFechaVencimiento(fechaVencimiento);
                liquidacion.setEstado("PENDIENTE");

                mapperPeriodo.asegurarPeriodo(periodo, idUsuario);
                mapper.insertarLiquidacion(liquidacion, idUsuario, ip);
                Integer idLiquidacion = liquidacion.getIdLiquidacion();

                // Paso 4: componer el detalle -- alquiler, consumo (luz/agua),
                // prorrateo y otros conceptos (RF-06 / CU-06).
                insertarDetalle(idLiquidacion, "Alquiler " + periodo, "ALQUILER", cv.getMontoAlquiler(), null, null, idUsuario, ip);
                if (montoLuz != null && montoLuz.compareTo(BigDecimal.ZERO) > 0) {
                    insertarDetalle(idLiquidacion, "Consumo de luz " + periodo, "LUZ", montoLuz, idProrrateoLuz, null, idUsuario, ip);
                }
                if (montoAgua != null && montoAgua.compareTo(BigDecimal.ZERO) > 0) {
                    insertarDetalle(idLiquidacion, "Consumo de agua " + periodo, "AGUA", montoAgua, idProrrateoAgua, null, idUsuario, ip);
                }
                for (MantenimientoImputableDTO m : mantenimientos) {
                    insertarDetalle(idLiquidacion, m.getDescripcion(), "MANTENIMIENTO", m.getMonto(), null, m.getIdMantenimiento(), idUsuario, ip);
                }

                BigDecimal montoTotal = mapper.obtenerMontoTotal(idLiquidacion);
                generadas.add(new LiquidacionGeneradaDTO(idLiquidacion, cv.getIdContrato(), cv.getNumeroUnidad(),
                        cv.getNombreInquilino(), periodo, cv.getMontoAlquiler(), liquidacion.getMontoLuz(),
                        liquidacion.getMontoAgua(), montoOtros, montoTotal));
            }

            // FA-01: unidades activas sin contrato vigente quedan reportadas como excepcion
            // (ya estan excluidas de contratosVigentes porque la consulta solo trae VIGENTE).
            for (String unidadSinContrato : mapper.listarNumeroUnidadSinContratoVigente()) {
                excepciones.add("Unidad " + unidadSinContrato + ": sin contrato vigente (FA-01), no se genero liquidacion.");
            }

            if (!generadas.isEmpty()) {
                mapperPeriodo.actualizarEstadoPorPeriodo(periodo, "LIQUIDADO");
            }

            GenerarLiquidacionResultDTO resultado = new GenerarLiquidacionResultDTO(generadas, excepciones, omitidas, advertencias);
            String mensaje = "Se generaron " + generadas.size() + " liquidacion(es) para " + periodo + ".";
            if (!omitidas.isEmpty()) {
                mensaje += " " + omitidas.size() + " omitida(s) por ya existir.";
            }
            if (!excepciones.isEmpty()) {
                mensaje += " " + excepciones.size() + " unidad(es) sin contrato vigente.";
            }
            return new ResponseDTO<>(resultado, false, mensaje);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> verificarVencidas() {
        return verificarVencidas(audit.usuarioActual(), audit.ipActual());
    }

    /**
     * Decision de diseno pendiente que se resuelve aca: una liquidacion
     * PENDIENTE/PARCIAL nunca pasaba sola a VENCIDO, se quedaba asi para
     * siempre salvo edicion manual. Se corre en dos vias, igual que RN-03
     * para contrato: bajo demanda (este metodo publico) y con una tarea
     * programada diaria, para que no dependa de que alguien abra el
     * dashboard. Se corre a las 6:15 a.m., 15 minutos despues de la de
     * contrato, para no chocar ambas tareas contra la base al mismo tiempo.
     */
    @Scheduled(cron = "0 15 6 * * *")
    public void tareaProgramadaVerificarVencidas() {
        ResponseDTO<Integer> resultado = verificarVencidas(null, "sistema (tarea programada)");
        if (resultado.isError()) {
            log.error("Fallo la verificacion programada de liquidaciones vencidas: {}", resultado.getMensaje());
        } else {
            log.info("Verificacion programada de liquidaciones vencidas: {}", resultado.getMensaje());
        }
    }

    private ResponseDTO<Integer> verificarVencidas(Integer idUsuario, String ip) {
        try {
            int marcadas = mapper.marcarLiquidacionesVencidas(idUsuario, ip);
            return new ResponseDTO<>(marcadas, false, marcadas + " liquidacion(es) marcada(s) como VENCIDO.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    private void insertarDetalle(Integer idLiquidacion, String concepto, String tipoConcepto, BigDecimal monto,
                                 Integer idProrrateo, Integer idMantenimiento, Integer idUsuario, String ip) {
        DetalleLiquidacionRequest detalle = new DetalleLiquidacionRequest();
        detalle.setIdLiquidacion(idLiquidacion);
        detalle.setConcepto(concepto);
        detalle.setTipoConcepto(tipoConcepto);
        detalle.setMonto(monto);
        detalle.setIdProrrateo(idProrrateo);
        detalle.setIdMantenimiento(idMantenimiento);
        mapperDetalle.insertarDetalle(detalle, idUsuario, ip);
    }
}
