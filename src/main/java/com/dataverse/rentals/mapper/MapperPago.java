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

import com.dataverse.rentals.model.pago.request.PagoRequest;
import com.dataverse.rentals.model.pago.response.PagoDTO;

@Mapper
public interface MapperPago {

    @Results(id = "mapListResultPago", value = {
        @Result(column = "id_pago", property = "idPago"),
        @Result(column = "id_liquidacion", property = "idLiquidacion"),
        @Result(column = "numero_unidad", property = "numeroUnidad"),
        @Result(column = "periodo", property = "periodo"),
        @Result(column = "id_usuario", property = "idUsuario"),
        @Result(column = "nombre_usuario", property = "nombreUsuario"),
        @Result(column = "fecha_pago", property = "fechaPago"),
        @Result(column = "monto_pagado", property = "montoPagado"),
        @Result(column = "metodo_pago", property = "metodoPago"),
        @Result(column = "numero_voucher", property = "numeroVoucher"),
        @Result(column = "ruta_evidencia", property = "rutaEvidencia")
    })
    @Select("""
        SELECT p.id_pago, p.id_liquidacion, u.numero_unidad, l.periodo, p.usuario_creacion AS id_usuario,
               us.nombre_completo AS nombre_usuario,
               p.fecha_pago, p.monto_pagado, p.metodo_pago, p.numero_voucher, p.ruta_evidencia
        FROM pago p
        JOIN liquidacion l ON l.id_liquidacion = p.id_liquidacion
        JOIN contrato c ON c.id_contrato = l.id_contrato
        JOIN unidad u ON u.id_unidad = c.id_unidad
        JOIN usuario us ON us.id_usuario = p.usuario_creacion
        WHERE p.estado_registro = 'ACTIVO'
        ORDER BY p.fecha_pago DESC
        """)
    List<PagoDTO> listarPagos();

    @Options(useGeneratedKeys = true, keyProperty = "args.idPago")
    @Insert("INSERT INTO pago (id_liquidacion, fecha_pago, monto_pagado, metodo_pago, numero_voucher, ruta_evidencia, "
            + "usuario_creacion, ip_creacion) "
            + "VALUES (#{args.idLiquidacion}, #{args.fechaPago}, #{args.montoPagado}, #{args.metodoPago}, "
            + "#{args.numeroVoucher}, #{args.rutaEvidencia}, #{idUsuario}, #{ip})")
    int insertarPago(@Param("args") PagoRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE pago SET id_liquidacion = #{args.idLiquidacion}, fecha_pago = #{args.fechaPago}, "
            + "monto_pagado = #{args.montoPagado}, metodo_pago = #{args.metodoPago}, "
            + "numero_voucher = #{args.numeroVoucher}, ruta_evidencia = #{args.rutaEvidencia}, "
            + "usuario_actualizacion = #{idUsuario}, fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_pago = #{args.idPago} AND estado_registro = 'ACTIVO'")
    int actualizarPago(@Param("args") PagoRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE pago SET estado_registro = 'ELIMINADO', usuario_eliminacion = #{idUsuario}, "
            + "fecha_eliminacion = NOW(), ip_eliminacion = #{ip} WHERE id_pago = #{idPago} AND estado_registro = 'ACTIVO'")
    int eliminarPago(@Param("idPago") Integer idPago, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Select("SELECT monto_total FROM liquidacion WHERE id_liquidacion = #{idLiquidacion} AND estado_registro = 'ACTIVO'")
    BigDecimal obtenerMontoTotalLiquidacion(@Param("idLiquidacion") Integer idLiquidacion);

    @Select("SELECT estado FROM liquidacion WHERE id_liquidacion = #{idLiquidacion} AND estado_registro = 'ACTIVO'")
    String obtenerEstadoLiquidacion(@Param("idLiquidacion") Integer idLiquidacion);

    @Select("SELECT COALESCE(SUM(monto_pagado), 0) FROM pago "
            + "WHERE id_liquidacion = #{idLiquidacion} AND estado_registro = 'ACTIVO' AND id_pago <> #{excluirIdPago}")
    BigDecimal totalPagadoLiquidacion(@Param("idLiquidacion") Integer idLiquidacion,
                                      @Param("excluirIdPago") Integer excluirIdPago);

    @Select("SELECT id_liquidacion FROM pago WHERE id_pago = #{idPago} AND estado_registro = 'ACTIVO'")
    Integer obtenerIdLiquidacionDePago(@Param("idPago") Integer idPago);

    @Update("""
        UPDATE liquidacion l
        SET l.estado = CASE
                WHEN COALESCE((SELECT SUM(p.monto_pagado) FROM pago p
                               WHERE p.id_liquidacion = l.id_liquidacion AND p.estado_registro = 'ACTIVO'), 0) >= l.monto_total
                    THEN 'PAGADO'
                WHEN l.estado = 'VENCIDO' THEN 'VENCIDO'
                WHEN COALESCE((SELECT SUM(p.monto_pagado) FROM pago p
                               WHERE p.id_liquidacion = l.id_liquidacion AND p.estado_registro = 'ACTIVO'), 0) > 0
                    THEN 'PARCIAL'
                ELSE 'PENDIENTE'
            END,
            l.usuario_actualizacion = #{idUsuario}, l.fecha_actualizacion = NOW(), l.ip_actualizacion = #{ip}
        WHERE l.id_liquidacion = #{idLiquidacion} AND l.estado_registro = 'ACTIVO' AND l.estado <> 'BORRADOR'
        """)
    int recalcularEstadoLiquidacion(@Param("idLiquidacion") Integer idLiquidacion,
                                    @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);
}
