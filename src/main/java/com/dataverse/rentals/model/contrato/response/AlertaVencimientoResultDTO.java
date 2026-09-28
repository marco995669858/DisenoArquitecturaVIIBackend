package com.dataverse.rentals.model.contrato.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Resultado de CU-03a -- Alertar Vencimiento de Contrato (RN-03). */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertaVencimientoResultDTO {
    /** Contratos que estaban VIGENTE y pasaron a VENCIDO en esta misma verificacion. */
    private Integer contratosMarcadosVencidos;
    /** Contratos VIGENTE cuya fecha_fin cae dentro de los dias de anticipacion configurados. */
    private List<ContratoPorVencerDTO> contratosPorVencer;
}
