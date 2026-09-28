package com.dataverse.rentals.service;

import java.util.List;

import com.dataverse.rentals.model.ResponseDTO;
import com.dataverse.rentals.model.contrato.request.ContratoRequest;
import com.dataverse.rentals.model.contrato.request.RenovarContratoRequest;
import com.dataverse.rentals.model.contrato.response.AlertaVencimientoResultDTO;
import com.dataverse.rentals.model.contrato.response.ContratoDTO;
import com.dataverse.rentals.model.contrato.response.RenovarContratoResultDTO;

public interface ServiceContrato {

    ResponseDTO<List<ContratoDTO>> listarContratos();

    ResponseDTO<Integer> insertUpdateContrato(ContratoRequest args);

    ResponseDTO<Integer> eliminarContrato(ContratoRequest args);

    /** RN-03 / CU-03a: marca vencidos los contratos que ya pasaron su fecha_fin y reporta los proximos a vencer. */
    ResponseDTO<AlertaVencimientoResultDTO> verificarVencimientos();

    /** CU-03b: cierra el contrato actual como RENOVADO y abre uno nuevo VIGENTE que lo continua. */
    ResponseDTO<RenovarContratoResultDTO> renovarContrato(RenovarContratoRequest args);
}
