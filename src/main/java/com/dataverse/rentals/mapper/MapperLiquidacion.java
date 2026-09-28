package com.dataverse.rentals.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.dataverse.rentals.model.liquidacion.request.LiquidacionRequest;
import com.dataverse.rentals.model.liquidacion.response.ContratoVigenteDTO;
import com.dataverse.rentals.model.liquidacion.response.LiquidacionDTO;
import com.dataverse.rentals.model.liquidacion.response.MantenimientoImputableDTO;

@Mapper
public interface MapperLiquidacion {

    @Results(id = "mapListResultLiquidacion", value = {
        @Result(column = "id_liquidacion", property = "idLiquidacion"),
        @Result(column = "id_contrato", property = "idContrato"),
        @Result(column = "numero_unidad", property = "numeroUnidad"),
        @Result(column = "nombre_inquilino", property = "nombreInquilino"),
        @Result(column = "periodo", property = "periodo"),
        @Result(column = "monto_alquiler", property = "montoAlquiler"),
        @Result(column = "monto_luz", property = "montoLuz"),
        @Result(column = "monto_agua", property = "montoAgua"),
        @Result(column = "monto_otros", property = "montoOtros"),
        @Result(column = "monto_total", property = "montoTotal"),
        @Result(column = "fecha_emision", property = "fechaEmision"),
        @Result(column = "fecha_vencimiento", property = "fechaVencimiento"),
        @Result(column = "estado", property = "estado")
    })
    @Select("""
        SELECT l.id_liquidacion, l.id_contrato, u.numero_unidad, i.nombre_completo AS nombre_inquilino,
               l.periodo, l.monto_alquiler, l.monto_luz, l.monto_agua, l.monto_otros, l.monto_total,
               l.fecha_emision, l.fecha_vencimiento, l.estado
        FROM liquidacion l
        JOIN contrato c ON c.id_contrato = l.id_contrato
        JOIN unidad u ON u.id_unidad = c.id_unidad
        JOIN inquilino i ON i.id_inquilino = c.id_inquilino
        WHERE l.estado_registro = 'ACTIVO'
        ORDER BY l.periodo DESC, u.numero_unidad
        """)
    List<LiquidacionDTO> listarLiquidaciones();

    @Options(useGeneratedKeys = true, keyProperty = "args.idLiquidacion")
    @Insert("INSERT INTO liquidacion (id_contrato, periodo, monto_alquiler, monto_luz, monto_agua, monto_otros, "
            + "fecha_emision, fecha_vencimiento, estado, usuario_creacion, ip_creacion) "
            + "VALUES (#{args.idContrato}, #{args.periodo}, #{args.montoAlquiler}, #{args.montoLuz}, #{args.montoAgua}, "
            + "#{args.montoOtros}, #{args.fechaEmision}, #{args.fechaVencimiento}, #{args.estado}, #{idUsuario}, #{ip})")
    int insertarLiquidacion(@Param("args") LiquidacionRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE liquidacion SET id_contrato = #{args.idContrato}, periodo = #{args.periodo}, "
            + "monto_alquiler = #{args.montoAlquiler}, monto_luz = #{args.montoLuz}, monto_agua = #{args.montoAgua}, "
            + "monto_otros = #{args.montoOtros}, fecha_emision = #{args.fechaEmision}, "
            + "fecha_vencimiento = #{args.fechaVencimiento}, estado = #{args.estado}, "
            + "usuario_actualizacion = #{idUsuario}, fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_liquidacion = #{args.idLiquidacion} AND estado_registro = 'ACTIVO'")
    int actualizarLiquidacion(@Param("args") LiquidacionRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE liquidacion SET estado_registro = 'ELIMINADO', usuario_eliminacion = #{idUsuario}, "
            + "fecha_eliminacion = NOW(), ip_eliminacion = #{ip} WHERE id_liquidacion = #{idLiquidacion} AND estado_registro = 'ACTIVO'")
    int eliminarLiquidacion(@Param("idLiquidacion") Integer idLiquidacion, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Select("SELECT COUNT(*) FROM detalle_liquidacion WHERE id_liquidacion = #{idLiquidacion} AND estado_registro = 'ACTIVO'")
    int contarDetallesDeLiquidacion(@Param("idLiquidacion") Integer idLiquidacion);

    @Select("SELECT COUNT(*) FROM pago WHERE id_liquidacion = #{idLiquidacion} AND estado_registro = 'ACTIVO'")
    int contarPagosDeLiquidacion(@Param("idLiquidacion") Integer idLiquidacion);

    // ------------------------------------------------------------------
    // Soporte para CU-06 -- Generar Liquidacion (generacion automatica)
    // ------------------------------------------------------------------

    @Results(id = "mapListResultContratoVigente", value = {
        @Result(column = "id_contrato", property = "idContrato"),
        @Result(column = "id_unidad", property = "idUnidad"),
        @Result(column = "numero_unidad", property = "numeroUnidad"),
        @Result(column = "id_inquilino", property = "idInquilino"),
        @Result(column = "nombre_inquilino", property = "nombreInquilino"),
        @Result(column = "monto_alquiler", property = "montoAlquiler")
    })
    @Select("""
        SELECT c.id_contrato, c.id_unidad, u.numero_unidad, c.id_inquilino, i.nombre_completo AS nombre_inquilino,
               c.monto_alquiler
        FROM contrato c
        JOIN unidad u ON u.id_unidad = c.id_unidad
        JOIN inquilino i ON i.id_inquilino = c.id_inquilino
        WHERE c.estado = 'VIGENTE' AND c.estado_registro = 'ACTIVO'
        ORDER BY u.numero_unidad
        """)
    List<ContratoVigenteDTO> listarContratosVigentes();

    // RN-04: no duplicar liquidacion para el mismo contrato y periodo.
    @Select("SELECT id_liquidacion FROM liquidacion WHERE id_contrato = #{idContrato} AND periodo = #{periodo} "
            + "AND estado_registro = 'ACTIVO'")
    Integer buscarLiquidacionExistente(@Param("idContrato") Integer idContrato, @Param("periodo") String periodo);

    @Results(id = "mapListResultMantenimientoImputable", value = {
        @Result(column = "id_mantenimiento", property = "idMantenimiento"),
        @Result(column = "descripcion", property = "descripcion"),
        @Result(column = "monto", property = "monto")
    })
    @Select("""
        SELECT m.id_mantenimiento, m.descripcion, m.monto
        FROM mantenimiento m
        WHERE m.id_unidad = #{idUnidad}
          AND DATE_FORMAT(m.fecha_mantenimiento, '%Y-%m') = #{periodo}
          AND m.imputable_inquilino = 1
          AND m.estado_registro = 'ACTIVO'
          AND NOT EXISTS (
            SELECT 1 FROM detalle_liquidacion d
            WHERE d.id_mantenimiento = m.id_mantenimiento AND d.estado_registro = 'ACTIVO'
          )
        """)
    List<MantenimientoImputableDTO> listarMantenimientoImputablePendiente(@Param("idUnidad") Integer idUnidad,
                                                                          @Param("periodo") String periodo);

    // monto_total es una columna GENERATED (STORED); no se inserta, se relee despues.
    @Select("SELECT monto_total FROM liquidacion WHERE id_liquidacion = #{idLiquidacion}")
    BigDecimal obtenerMontoTotal(@Param("idLiquidacion") Integer idLiquidacion);

    // FA-01: unidades activas que no tienen contrato vigente, para reportarlas como excepcion.
    @Select("""
        SELECT u.numero_unidad
        FROM unidad u
        WHERE u.estado_registro = 'ACTIVO'
          AND NOT EXISTS (
            SELECT 1 FROM contrato c
            WHERE c.id_unidad = u.id_unidad AND c.estado = 'VIGENTE' AND c.estado_registro = 'ACTIVO'
          )
        ORDER BY u.numero_unidad
        """)
    List<String> listarNumeroUnidadSinContratoVigente();

    // ------------------------------------------------------------------
    // Estado VENCIDO de la liquidacion -- misma idea que RN-03/CU-03a para
    // contrato: una liquidacion PENDIENTE o PARCIAL cuya fecha_vencimiento ya
    // paso se marca VENCIDO. Si despues llega un pago que cubre el total,
    // recalcularEstadoLiquidacion (MapperPago) la mueve a PAGADO igual desde
    // VENCIDO; si el pago es parcial, se queda en VENCIDO (no retrocede a
    // PARCIAL) porque esa consulta ya respeta el estado VENCIDO existente.
    @Update("UPDATE liquidacion SET estado = 'VENCIDO', usuario_actualizacion = #{idUsuario}, "
            + "fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE estado IN ('PENDIENTE', 'PARCIAL') AND estado_registro = 'ACTIVO' AND fecha_vencimiento < CURDATE()")
    int marcarLiquidacionesVencidas(@Param("idUsuario") Integer idUsuario, @Param("ip") String ip);
}
