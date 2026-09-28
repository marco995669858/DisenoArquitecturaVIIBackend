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

import com.dataverse.rentals.model.inquilino.request.InquilinoRequest;
import com.dataverse.rentals.model.inquilino.response.InquilinoDTO;

@Mapper
public interface MapperInquilino {

    @Results(id = "mapListResultInquilino", value = {
        @Result(column = "id_inquilino", property = "idInquilino"),
        @Result(column = "tipo_documento", property = "tipoDocumento"),
        @Result(column = "numero_documento", property = "numeroDocumento"),
        @Result(column = "nombre_completo", property = "nombreCompleto"),
        @Result(column = "telefono", property = "telefono"),
        @Result(column = "email", property = "email"),
        @Result(column = "direccion", property = "direccion")
    })
    @Select("SELECT id_inquilino, tipo_documento, numero_documento, nombre_completo, telefono, email, direccion "
            + "FROM inquilino WHERE estado_registro = 'ACTIVO' ORDER BY nombre_completo")
    List<InquilinoDTO> listarInquilinos();

    @Options(useGeneratedKeys = true, keyProperty = "args.idInquilino")
    @Insert("INSERT INTO inquilino (tipo_documento, numero_documento, nombre_completo, telefono, email, direccion, "
            + "usuario_creacion, ip_creacion) "
            + "VALUES (#{args.tipoDocumento}, #{args.numeroDocumento}, #{args.nombreCompleto}, #{args.telefono}, "
            + "#{args.email}, #{args.direccion}, #{idUsuario}, #{ip})")
    int insertarInquilino(@Param("args") InquilinoRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE inquilino SET tipo_documento = #{args.tipoDocumento}, numero_documento = #{args.numeroDocumento}, "
            + "nombre_completo = #{args.nombreCompleto}, telefono = #{args.telefono}, email = #{args.email}, "
            + "direccion = #{args.direccion}, usuario_actualizacion = #{idUsuario}, fecha_actualizacion = NOW(), "
            + "ip_actualizacion = #{ip} WHERE id_inquilino = #{args.idInquilino} AND estado_registro = 'ACTIVO'")
    int actualizarInquilino(@Param("args") InquilinoRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE inquilino SET estado_registro = 'ELIMINADO', usuario_eliminacion = #{idUsuario}, "
            + "fecha_eliminacion = NOW(), ip_eliminacion = #{ip} WHERE id_inquilino = #{idInquilino} AND estado_registro = 'ACTIVO'")
    int eliminarInquilino(@Param("idInquilino") Integer idInquilino, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Select("SELECT COUNT(*) FROM contrato WHERE id_inquilino = #{idInquilino} AND estado_registro = 'ACTIVO'")
    int contarContratosDeInquilino(@Param("idInquilino") Integer idInquilino);
}
