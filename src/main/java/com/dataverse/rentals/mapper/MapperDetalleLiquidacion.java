package com.dataverse.rentals.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.dataverse.rentals.model.detalleLiquidacion.request.DetalleLiquidacionRequest;
import com.dataverse.rentals.model.detalleLiquidacion.response.DetalleLiquidacionDTO;

@Mapper
public interface MapperDetalleLiquidacion {

    @Results(id = "mapListResultDetalleLiquidacion", value = {
        @Result(column = "id_detalle", property = "idDetalle"),
        @Result(column = "id_liquidacion", property = "idLiquidacion"),
        @Result(column = "concepto", property = "concepto"),
        @Result(column = "tipo_concepto", property = "tipoConcepto"),
        @Result(column = "monto", property = "monto"),
        @Result(column = "id_prorrateo", property = "idProrrateo"),
        @Result(column = "id_mantenimiento", property = "idMantenimiento")
    })
    @Select("SELECT id_detalle, id_liquidacion, concepto, tipo_concepto, monto, id_prorrateo, id_mantenimiento "
            + "FROM detalle_liquidacion WHERE id_liquidacion = #{idLiquidacion} AND estado_registro = 'ACTIVO'")
    List<DetalleLiquidacionDTO> listarPorLiquidacion(@Param("idLiquidacion") Integer idLiquidacion);

    @Options(useGeneratedKeys = true, keyProperty = "args.idDetalle")
    @Insert("INSERT INTO detalle_liquidacion (id_liquidacion, concepto, tipo_concepto, monto, id_prorrateo, "
            + "id_mantenimiento, usuario_creacion, ip_creacion) "
            + "VALUES (#{args.idLiquidacion}, #{args.concepto}, #{args.tipoConcepto}, #{args.monto}, "
            + "#{args.idProrrateo}, #{args.idMantenimiento}, #{idUsuario}, #{ip})")
    int insertarDetalle(@Param("args") DetalleLiquidacionRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE detalle_liquidacion SET concepto = #{args.concepto}, tipo_concepto = #{args.tipoConcepto}, "
            + "monto = #{args.monto}, id_prorrateo = #{args.idProrrateo}, id_mantenimiento = #{args.idMantenimiento}, "
            + "usuario_actualizacion = #{idUsuario}, fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_detalle = #{args.idDetalle} AND estado_registro = 'ACTIVO'")
    int actualizarDetalle(@Param("args") DetalleLiquidacionRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE detalle_liquidacion SET estado_registro = 'ELIMINADO', usuario_eliminacion = #{idUsuario}, "
            + "fecha_eliminacion = NOW(), ip_eliminacion = #{ip} WHERE id_detalle = #{idDetalle} AND estado_registro = 'ACTIVO'")
    int eliminarDetalle(@Param("idDetalle") Integer idDetalle, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);
}
