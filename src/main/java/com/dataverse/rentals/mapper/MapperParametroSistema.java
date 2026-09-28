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

import com.dataverse.rentals.model.parametroSistema.request.ParametroSistemaRequest;
import com.dataverse.rentals.model.parametroSistema.response.ParametroSistemaDTO;

@Mapper
public interface MapperParametroSistema {

    @Results(id = "mapListResultParametroSistema", value = {
        @Result(column = "id_parametro", property = "idParametro"),
        @Result(column = "clave", property = "clave"),
        @Result(column = "valor", property = "valor"),
        @Result(column = "descripcion", property = "descripcion")
    })
    @Select("SELECT id_parametro, clave, valor, descripcion FROM parametro_sistema "
            + "WHERE estado_registro = 'ACTIVO' ORDER BY clave")
    List<ParametroSistemaDTO> listarParametros();

    @Select("SELECT valor FROM parametro_sistema WHERE clave = #{clave} AND estado_registro = 'ACTIVO'")
    String obtenerValorPorClave(@Param("clave") String clave);

    @Options(useGeneratedKeys = true, keyProperty = "args.idParametro")
    @Insert("INSERT INTO parametro_sistema (clave, valor, descripcion, usuario_creacion, ip_creacion) "
            + "VALUES (#{args.clave}, #{args.valor}, #{args.descripcion}, #{idUsuario}, #{ip})")
    int insertarParametro(@Param("args") ParametroSistemaRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    // La clave no se puede editar: es lo que otras partes del sistema usan para
    // ubicar el parametro (por ejemplo, "DIAS_ALERTA_VENCIMIENTO"). Solo se
    // actualiza el valor y la descripcion.
    @Update("UPDATE parametro_sistema SET valor = #{args.valor}, descripcion = #{args.descripcion}, "
            + "usuario_actualizacion = #{idUsuario}, fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_parametro = #{args.idParametro} AND estado_registro = 'ACTIVO'")
    int actualizarParametro(@Param("args") ParametroSistemaRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE parametro_sistema SET estado_registro = 'ELIMINADO', usuario_eliminacion = #{idUsuario}, "
            + "fecha_eliminacion = NOW(), ip_eliminacion = #{ip} WHERE id_parametro = #{idParametro} AND estado_registro = 'ACTIVO'")
    int eliminarParametro(@Param("idParametro") Integer idParametro, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);
}
