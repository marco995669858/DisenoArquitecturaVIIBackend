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

import com.dataverse.rentals.model.reciboServicio.request.ReciboServicioRequest;
import com.dataverse.rentals.model.reciboServicio.response.ReciboServicioDTO;

@Mapper
public interface MapperReciboServicio {

    @Results(id = "mapListResultReciboServicio", value = {
        @Result(column = "id_recibo", property = "idRecibo"),
        @Result(column = "periodo", property = "periodo"),
        @Result(column = "tipo_servicio", property = "tipoServicio"),
        @Result(column = "monto_total", property = "montoTotal"),
        @Result(column = "fecha_emision", property = "fechaEmision"),
        @Result(column = "fecha_vencimiento", property = "fechaVencimiento"),
        @Result(column = "ruta_documento", property = "rutaDocumento")
    })
    @Select("SELECT id_recibo, periodo, tipo_servicio, monto_total, fecha_emision, fecha_vencimiento, ruta_documento "
            + "FROM recibo_servicio WHERE estado_registro = 'ACTIVO' ORDER BY periodo DESC, tipo_servicio")
    List<ReciboServicioDTO> listarRecibos();

    @Options(useGeneratedKeys = true, keyProperty = "args.idRecibo")
    @Insert("INSERT INTO recibo_servicio (periodo, tipo_servicio, monto_total, fecha_emision, fecha_vencimiento, "
            + "ruta_documento, usuario_creacion, ip_creacion) "
            + "VALUES (#{args.periodo}, #{args.tipoServicio}, #{args.montoTotal}, #{args.fechaEmision}, "
            + "#{args.fechaVencimiento}, #{args.rutaDocumento}, #{idUsuario}, #{ip})")
    int insertarRecibo(@Param("args") ReciboServicioRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE recibo_servicio SET periodo = #{args.periodo}, tipo_servicio = #{args.tipoServicio}, "
            + "monto_total = #{args.montoTotal}, fecha_emision = #{args.fechaEmision}, "
            + "fecha_vencimiento = #{args.fechaVencimiento}, ruta_documento = #{args.rutaDocumento}, "
            + "usuario_actualizacion = #{idUsuario}, fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_recibo = #{args.idRecibo} AND estado_registro = 'ACTIVO'")
    int actualizarRecibo(@Param("args") ReciboServicioRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE recibo_servicio SET estado_registro = 'ELIMINADO', usuario_eliminacion = #{idUsuario}, "
            + "fecha_eliminacion = NOW(), ip_eliminacion = #{ip} WHERE id_recibo = #{idRecibo} AND estado_registro = 'ACTIVO'")
    int eliminarRecibo(@Param("idRecibo") Integer idRecibo, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Select("SELECT COUNT(*) FROM prorrateo WHERE id_recibo = #{idRecibo} AND estado_registro = 'ACTIVO'")
    int contarProrrateosDeRecibo(@Param("idRecibo") Integer idRecibo);
}
