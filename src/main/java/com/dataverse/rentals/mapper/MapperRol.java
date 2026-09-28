package com.dataverse.rentals.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.dataverse.rentals.model.modulo.response.PermisoAsignadoDTO;
import com.dataverse.rentals.model.rol.request.RolRequest;
import com.dataverse.rentals.model.rol.response.RolDTO;

@Mapper
public interface MapperRol {

    @Results(id = "mapListResultRol", value = {
        @Result(column = "id_rol", property = "idRol"),
        @Result(column = "nombre", property = "nombre")
    })
    @Select("SELECT id_rol, nombre FROM rol WHERE estado_registro = 'ACTIVO' ORDER BY nombre")
    List<RolDTO> listarRoles();

    @Options(useGeneratedKeys = true, keyProperty = "args.idRol")
    @Insert("INSERT INTO rol (nombre, usuario_creacion, ip_creacion) "
            + "VALUES (#{args.nombre}, #{idUsuario}, #{ip})")
    int insertarRol(@Param("args") RolRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE rol SET nombre = #{args.nombre}, usuario_actualizacion = #{idUsuario}, "
            + "fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_rol = #{args.idRol} AND estado_registro = 'ACTIVO'")
    int actualizarRol(@Param("args") RolRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE rol SET estado_registro = 'ELIMINADO', usuario_eliminacion = #{idUsuario}, "
            + "fecha_eliminacion = NOW(), ip_eliminacion = #{ip} WHERE id_rol = #{idRol} AND estado_registro = 'ACTIVO'")
    int eliminarRol(@Param("idRol") Integer idRol, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Select("SELECT COUNT(*) FROM usuario WHERE id_rol = #{idRol} AND estado_registro = 'ACTIVO'")
    int contarUsuariosDeRol(@Param("idRol") Integer idRol);

    // ------------------------------------------------------------------
    // Permisos por rol (que modulos puede ver cada rol) -- ver modulo_sistema
    // / rol_permiso en el schema y ImplRol.insertUpdateRol.
    // ------------------------------------------------------------------

    @Results(id = "mapPermisoAsignado", value = {
        @Result(column = "codigo", property = "codigo"),
        @Result(column = "nivel_acceso", property = "nivelAcceso")
    })
    @Select("SELECT m.codigo, rp.nivel_acceso FROM rol_permiso rp JOIN modulo_sistema m ON m.id_modulo = rp.id_modulo "
            + "WHERE rp.id_rol = #{idRol} ORDER BY m.orden")
    List<PermisoAsignadoDTO> listarPermisosDeRol(@Param("idRol") Integer idRol);

    @Delete("DELETE FROM rol_permiso WHERE id_rol = #{idRol}")
    int eliminarPermisosDeRol(@Param("idRol") Integer idRol);

    @Insert("INSERT INTO rol_permiso (id_rol, id_modulo, nivel_acceso) SELECT #{idRol}, id_modulo, #{nivelAcceso} FROM modulo_sistema WHERE codigo = #{codigo}")
    int insertarPermiso(@Param("idRol") Integer idRol, @Param("codigo") String codigo, @Param("nivelAcceso") String nivelAcceso);
}
