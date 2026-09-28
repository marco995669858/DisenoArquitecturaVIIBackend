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

import com.dataverse.rentals.model.mantenimiento.request.MantenimientoRequest;
import com.dataverse.rentals.model.mantenimiento.response.MantenimientoDTO;

@Mapper
public interface MapperMantenimiento {

    @Results(id = "mapListResultMantenimiento", value = {
        @Result(column = "id_mantenimiento", property = "idMantenimiento"),
        @Result(column = "id_unidad", property = "idUnidad"),
        @Result(column = "numero_unidad", property = "numeroUnidad"),
        @Result(column = "ubicacion", property = "ubicacion"),
        @Result(column = "id_usuario", property = "idUsuario"),
        @Result(column = "nombre_usuario", property = "nombreUsuario"),
        @Result(column = "descripcion", property = "descripcion"),
        @Result(column = "fecha_mantenimiento", property = "fechaMantenimiento"),
        @Result(column = "tipo_gasto", property = "tipoGasto"),
        @Result(column = "proveedor", property = "proveedor"),
        @Result(column = "monto_materiales", property = "montoMateriales"),
        @Result(column = "monto_mano_obra", property = "montoManoObra"),
        @Result(column = "monto", property = "monto"),
        @Result(column = "imputable_inquilino", property = "imputableInquilino")
    })
    @Select("""
        SELECT m.id_mantenimiento, m.id_unidad, u.numero_unidad, m.ubicacion, m.usuario_creacion AS id_usuario,
               us.nombre_completo AS nombre_usuario,
               m.descripcion, m.fecha_mantenimiento, m.tipo_gasto, m.proveedor,
               m.monto_materiales, m.monto_mano_obra, m.monto, m.imputable_inquilino
        FROM mantenimiento m
        LEFT JOIN unidad u ON u.id_unidad = m.id_unidad
        JOIN usuario us ON us.id_usuario = m.usuario_creacion
        WHERE m.estado_registro = 'ACTIVO'
        ORDER BY m.fecha_mantenimiento DESC
        """)
    List<MantenimientoDTO> listarMantenimientos();

    @Options(useGeneratedKeys = true, keyProperty = "args.idMantenimiento")
    @Insert("INSERT INTO mantenimiento (id_unidad, ubicacion, descripcion, fecha_mantenimiento, tipo_gasto, "
            + "proveedor, monto_materiales, monto_mano_obra, monto, imputable_inquilino, usuario_creacion, ip_creacion) "
            + "VALUES (#{args.idUnidad}, #{args.ubicacion}, #{args.descripcion}, #{args.fechaMantenimiento}, "
            + "#{args.tipoGasto}, #{args.proveedor}, #{args.montoMateriales}, #{args.montoManoObra}, #{args.monto}, "
            + "#{args.imputableInquilino}, #{idUsuario}, #{ip})")
    int insertarMantenimiento(@Param("args") MantenimientoRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE mantenimiento SET id_unidad = #{args.idUnidad}, ubicacion = #{args.ubicacion}, "
            + "descripcion = #{args.descripcion}, "
            + "fecha_mantenimiento = #{args.fechaMantenimiento}, tipo_gasto = #{args.tipoGasto}, "
            + "proveedor = #{args.proveedor}, monto_materiales = #{args.montoMateriales}, "
            + "monto_mano_obra = #{args.montoManoObra}, monto = #{args.monto}, "
            + "imputable_inquilino = #{args.imputableInquilino}, usuario_actualizacion = #{idUsuario}, "
            + "fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_mantenimiento = #{args.idMantenimiento} AND estado_registro = 'ACTIVO'")
    int actualizarMantenimiento(@Param("args") MantenimientoRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE mantenimiento SET estado_registro = 'ELIMINADO', usuario_eliminacion = #{idUsuario}, "
            + "fecha_eliminacion = NOW(), ip_eliminacion = #{ip} WHERE id_mantenimiento = #{idMantenimiento} AND estado_registro = 'ACTIVO'")
    int eliminarMantenimiento(@Param("idMantenimiento") Integer idMantenimiento, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);
}
