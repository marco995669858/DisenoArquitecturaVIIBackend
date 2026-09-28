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

import com.dataverse.rentals.model.medidor.request.MedidorRequest;
import com.dataverse.rentals.model.medidor.response.MedidorDTO;

@Mapper
public interface MapperMedidor {

    @Results(id = "mapListResultMedidor", value = {
        @Result(column = "id_medidor", property = "idMedidor"),
        @Result(column = "id_unidad", property = "idUnidad"),
        @Result(column = "numero_unidad", property = "numeroUnidad"),
        @Result(column = "tipo_servicio", property = "tipoServicio"),
        @Result(column = "tipo_medidor", property = "tipoMedidor"),
        @Result(column = "numero_serie", property = "numeroSerie")
    })
    @Select("""
        SELECT m.id_medidor, m.id_unidad, u.numero_unidad, m.tipo_servicio, m.tipo_medidor, m.numero_serie
        FROM medidor m
        LEFT JOIN unidad u ON u.id_unidad = m.id_unidad
        WHERE m.estado_registro = 'ACTIVO'
        ORDER BY m.tipo_medidor, m.tipo_servicio
        """)
    List<MedidorDTO> listarMedidores();

    @Options(useGeneratedKeys = true, keyProperty = "args.idMedidor")
    @Insert("INSERT INTO medidor (id_unidad, tipo_servicio, tipo_medidor, numero_serie, usuario_creacion, ip_creacion) "
            + "VALUES (#{args.idUnidad}, #{args.tipoServicio}, #{args.tipoMedidor}, #{args.numeroSerie}, #{idUsuario}, #{ip})")
    int insertarMedidor(@Param("args") MedidorRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE medidor SET id_unidad = #{args.idUnidad}, tipo_servicio = #{args.tipoServicio}, "
            + "tipo_medidor = #{args.tipoMedidor}, numero_serie = #{args.numeroSerie}, "
            + "usuario_actualizacion = #{idUsuario}, fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_medidor = #{args.idMedidor} AND estado_registro = 'ACTIVO'")
    int actualizarMedidor(@Param("args") MedidorRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE medidor SET estado_registro = 'ELIMINADO', usuario_eliminacion = #{idUsuario}, "
            + "fecha_eliminacion = NOW(), ip_eliminacion = #{ip} WHERE id_medidor = #{idMedidor} AND estado_registro = 'ACTIVO'")
    int eliminarMedidor(@Param("idMedidor") Integer idMedidor, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Select("SELECT COUNT(*) FROM lectura_medidor WHERE id_medidor = #{idMedidor} AND estado_registro = 'ACTIVO'")
    int contarLecturasDeMedidor(@Param("idMedidor") Integer idMedidor);
}
