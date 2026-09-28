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

import com.dataverse.rentals.model.lecturaMedidor.request.LecturaMedidorRequest;
import com.dataverse.rentals.model.lecturaMedidor.response.LecturaMedidorDTO;

@Mapper
public interface MapperLecturaMedidor {

    @Results(id = "mapListResultLecturaMedidor", value = {
        @Result(column = "id_lectura", property = "idLectura"),
        @Result(column = "id_medidor", property = "idMedidor"),
        @Result(column = "numero_serie", property = "numeroSerie"),
        @Result(column = "tipo_servicio", property = "tipoServicio"),
        @Result(column = "numero_unidad", property = "numeroUnidad"),
        @Result(column = "id_usuario", property = "idUsuario"),
        @Result(column = "periodo", property = "periodo"),
        @Result(column = "lectura_anterior", property = "lecturaAnterior"),
        @Result(column = "lectura_actual", property = "lecturaActual"),
        @Result(column = "consumo", property = "consumo"),
        @Result(column = "fecha_lectura", property = "fechaLectura")
    })
    @Select("""
        SELECT l.id_lectura, l.id_medidor, m.numero_serie, m.tipo_servicio, u.numero_unidad,
               l.usuario_creacion AS id_usuario, l.periodo, l.lectura_anterior, l.lectura_actual,
               (l.lectura_actual - l.lectura_anterior) AS consumo, l.fecha_lectura
        FROM lectura_medidor l
        JOIN medidor m ON m.id_medidor = l.id_medidor
        LEFT JOIN unidad u ON u.id_unidad = m.id_unidad
        WHERE l.estado_registro = 'ACTIVO'
        ORDER BY l.periodo DESC, m.numero_serie
        """)
    List<LecturaMedidorDTO> listarLecturas();

    @Options(useGeneratedKeys = true, keyProperty = "args.idLectura")
    @Insert("INSERT INTO lectura_medidor (id_medidor, periodo, lectura_anterior, lectura_actual, fecha_lectura, "
            + "usuario_creacion, ip_creacion) "
            + "VALUES (#{args.idMedidor}, #{args.periodo}, #{args.lecturaAnterior}, #{args.lecturaActual}, "
            + "#{args.fechaLectura}, #{idUsuario}, #{ip})")
    int insertarLectura(@Param("args") LecturaMedidorRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE lectura_medidor SET id_medidor = #{args.idMedidor}, periodo = #{args.periodo}, "
            + "lectura_anterior = #{args.lecturaAnterior}, lectura_actual = #{args.lecturaActual}, "
            + "fecha_lectura = #{args.fechaLectura}, usuario_actualizacion = #{idUsuario}, "
            + "fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_lectura = #{args.idLectura} AND estado_registro = 'ACTIVO'")
    int actualizarLectura(@Param("args") LecturaMedidorRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE lectura_medidor SET estado_registro = 'ELIMINADO', usuario_eliminacion = #{idUsuario}, "
            + "fecha_eliminacion = NOW(), ip_eliminacion = #{ip} WHERE id_lectura = #{idLectura} AND estado_registro = 'ACTIVO'")
    int eliminarLectura(@Param("idLectura") Integer idLectura, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);
}
