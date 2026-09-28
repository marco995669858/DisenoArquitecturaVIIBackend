package com.dataverse.rentals.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataverse.rentals.mapper.MapperParametroSistema;
import com.dataverse.rentals.mapper.MapperPeriodoFacturacion;
import com.dataverse.rentals.mapper.MapperProrrateo;
import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.prorrateo.request.CalcularProrrateoRequest;
import com.dataverse.rentals.model.prorrateo.request.ProrrateoRequest;
import com.dataverse.rentals.model.prorrateo.response.ConsumoUnidadDTO;
import com.dataverse.rentals.model.prorrateo.response.ProrrateoDTO;
import com.dataverse.rentals.security.AuditContext;
import com.dataverse.rentals.util.ErrorMessages;
import com.dataverse.rentals.service.ServiceProrrateo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImplProrrateo implements ServiceProrrateo {

    private final MapperProrrateo mapper;
    private final MapperPeriodoFacturacion mapperPeriodo;
    private final MapperParametroSistema mapperParametro;
    private final AuditContext audit;

    @Override
    public ResponseDTO<List<ProrrateoDTO>> listarProrrateos() {
        try {
            List<ProrrateoDTO> data = mapper.listarProrrateos();
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
    public ResponseDTO<Integer> insertUpdateProrrateo(ProrrateoRequest args) {
        try {
            if (args.getIdProrrateo() == null) {
                Integer idUsuario = audit.usuarioActual();
                mapperPeriodo.asegurarPeriodo(args.getPeriodo(), idUsuario);
                mapper.insertarProrrateo(args, idUsuario, audit.ipActual());
                return new ResponseDTO<>(args.getIdProrrateo(), false, "Prorrateo registrado correctamente.");
            }
            mapper.actualizarProrrateo(args, audit.usuarioActual(), audit.ipActual());
            return new ResponseDTO<>(args.getIdProrrateo(), false, "Prorrateo actualizado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    @Override
    public ResponseDTO<Integer> eliminarProrrateo(ProrrateoRequest args) {
        try {
            int filas = mapper.eliminarProrrateo(args.getIdProrrateo(), audit.usuarioActual(), audit.ipActual());
            if (filas <= 0) {
                return new ResponseDTO<>(true, "No se encontró el prorrateo a eliminar.");
            }
            return new ResponseDTO<>(filas, false, "Prorrateo eliminado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }

    /**
     * CU-05 -- Calcular Prorrateo. No tiene actor directo: hoy lo dispara el
     * Administrador desde la pantalla de prorrateo porque CU-06 (Generar
     * Liquidacion) todavia no lo invoca automaticamente; cuando CU-06 llame a
     * este mismo metodo, el flujo quedara tal como lo describe el analisis.
     */
    @Override
    @Transactional
    public ResponseDTO<List<ProrrateoDTO>> calcularProrrateo(CalcularProrrateoRequest args) {
        try {
            String periodo = args.getPeriodo();
            String tipoServicio = args.getTipoServicio();
            if (periodo == null || periodo.isBlank() || tipoServicio == null || tipoServicio.isBlank()) {
                return new ResponseDTO<>(true, "Debe indicar el periodo y el tipo de servicio (LUZ o AGUA).");
            }

            // Precondicion (CU-05): las lecturas del periodo deben estar completas
            // (postcondicion de CU-04) -- tanto la del medidor general como la de
            // todas las unidades con medidor individual de ese servicio.
            BigDecimal consumoGeneral = mapper.obtenerConsumoGeneral(periodo, tipoServicio);
            if (consumoGeneral == null || consumoGeneral.compareTo(BigDecimal.ZERO) < 0) {
                return new ResponseDTO<>(true, "No se ha registrado la lectura del medidor general de "
                        + tipoServicio + " para el periodo " + periodo + ".");
            }

            int unidadesConMedidor = mapper.contarUnidadesConMedidorIndividual(tipoServicio);
            if (unidadesConMedidor == 0) {
                return new ResponseDTO<>(true, "No hay unidades con medidor individual de " + tipoServicio + " registrado.");
            }
            int unidadesConLectura = mapper.contarUnidadesConLecturaIndividual(periodo, tipoServicio);
            if (unidadesConLectura < unidadesConMedidor) {
                return new ResponseDTO<>(true, "El periodo esta incompleto (FA-02): tienen lectura " + unidadesConLectura
                        + " de " + unidadesConMedidor + " unidades con medidor de " + tipoServicio
                        + ". Complete las lecturas antes de calcular el prorrateo.");
            }

            // El costo unitario sale del recibo del servicio, no se inventa.
            BigDecimal montoRecibo = mapper.obtenerMontoTotalRecibo(periodo, tipoServicio);
            if (montoRecibo == null) {
                return new ResponseDTO<>(true, "No se ha registrado el recibo de " + tipoServicio
                        + " del periodo " + periodo + "; hace falta para calcular el costo unitario.");
            }
            Integer idRecibo = mapper.obtenerIdRecibo(periodo, tipoServicio);

            List<ConsumoUnidadDTO> consumos = mapper.listarConsumosIndividuales(periodo, tipoServicio);
            BigDecimal sumaIndividual = BigDecimal.ZERO;
            for (ConsumoUnidadDTO c : consumos) {
                sumaIndividual = sumaIndividual.add(c.getConsumoIndividual());
            }

            // Excedente = consumo general - suma de individuales (RN-01).
            BigDecimal excedente = consumoGeneral.subtract(sumaIndividual);
            if (excedente.compareTo(BigDecimal.ZERO) < 0) {
                // FA-01: no calculamos con datos inconsistentes. (No se agrega un
                // estado "observado" al periodo: eso es una decision de modelo de
                // datos aparte que no se tomo en esta iteracion; por ahora basta
                // con rechazar el calculo y pedir corregir el dato de origen.)
                return new ResponseDTO<>(true, "FA-01: la suma de consumos individuales (" + sumaIndividual
                        + ") supera al consumo del medidor general (" + consumoGeneral
                        + "). Revise las lecturas antes de calcular el prorrateo.");
            }
            if (consumoGeneral.compareTo(BigDecimal.ZERO) == 0) {
                return new ResponseDTO<>(true, "El consumo del medidor general es cero; no se puede calcular un costo unitario.");
            }

            BigDecimal costoUnitario = montoRecibo.divide(consumoGeneral, 4, RoundingMode.HALF_UP);

            // S-04: criterio de reparto configurable (parametro_sistema); por
            // defecto, proporcional al consumo individual.
            // Si el parametro no esta configurado, "CONSUMO" es el criterio por defecto.
            String criterio = mapperParametro.obtenerValorPorClave("CRITERIO_PRORRATEO");
            boolean porArea = "AREA".equalsIgnoreCase(criterio);
            BigDecimal baseTotal = BigDecimal.ZERO;
            for (ConsumoUnidadDTO c : consumos) {
                baseTotal = baseTotal.add(porArea ? c.getAreaM2() : c.getConsumoIndividual());
            }

            Integer idUsuario = audit.usuarioActual();
            String ip = audit.ipActual();
            List<ProrrateoDTO> resultado = new ArrayList<>();
            BigDecimal acumulado = BigDecimal.ZERO;

            for (int i = 0; i < consumos.size(); i++) {
                ConsumoUnidadDTO c = consumos.get(i);
                BigDecimal consumoComunAsignado;
                if (i == consumos.size() - 1) {
                    // La ultima unidad se lleva el resto del redondeo, para que la
                    // suma de lo asignado cierre exactamente con el excedente.
                    consumoComunAsignado = excedente.subtract(acumulado).setScale(2, RoundingMode.HALF_UP);
                } else if (baseTotal.compareTo(BigDecimal.ZERO) == 0) {
                    consumoComunAsignado = BigDecimal.ZERO;
                } else {
                    BigDecimal base = porArea ? c.getAreaM2() : c.getConsumoIndividual();
                    consumoComunAsignado = excedente.multiply(base).divide(baseTotal, 2, RoundingMode.HALF_UP);
                    acumulado = acumulado.add(consumoComunAsignado);
                }

                BigDecimal montoCalculado = c.getConsumoIndividual().add(consumoComunAsignado)
                        .multiply(costoUnitario).setScale(2, RoundingMode.HALF_UP);

                ProrrateoRequest req = new ProrrateoRequest();
                req.setIdUnidad(c.getIdUnidad());
                req.setIdRecibo(idRecibo);
                req.setPeriodo(periodo);
                req.setTipoServicio(tipoServicio);
                req.setConsumoIndividual(c.getConsumoIndividual());
                req.setConsumoComunAsignado(consumoComunAsignado);
                req.setCostoUnitario(costoUnitario);
                req.setMontoCalculado(montoCalculado);

                Integer idExistente = mapper.buscarIdProrrateoExistente(c.getIdUnidad(), periodo, tipoServicio);
                if (idExistente == null) {
                    mapperPeriodo.asegurarPeriodo(periodo, idUsuario);
                    mapper.insertarProrrateo(req, idUsuario, ip);
                    idExistente = req.getIdProrrateo();
                } else {
                    req.setIdProrrateo(idExistente);
                    mapper.actualizarProrrateo(req, idUsuario, ip);
                }

                resultado.add(new ProrrateoDTO(idExistente, c.getIdUnidad(), c.getNumeroUnidad(), idRecibo, periodo,
                        tipoServicio, c.getConsumoIndividual(), consumoComunAsignado, costoUnitario, montoCalculado));
            }

            return new ResponseDTO<>(resultado, false,
                    "Prorrateo de " + tipoServicio + " calculado para " + resultado.size() + " unidad(es).");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseDTO<>(true, ErrorMessages.resolver(e));
        }
    }
}
