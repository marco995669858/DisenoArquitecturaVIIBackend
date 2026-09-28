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
import com.dataverse.rentals.model.usuario.request.UsuarioRequest;
import com.dataverse.rentals.model.usuario.response.EstadoMfaInternoDTO;
import com.dataverse.rentals.model.usuario.response.RefreshTokenDTO;
import com.dataverse.rentals.model.usuario.response.UsuarioDTO;
import com.dataverse.rentals.model.usuario.response.UsuarioLoginDTO;

@Mapper
public interface MapperUsuario {

    @Results(id = "mapUsuarioLogin", value = {
        @Result(column = "id_usuario", property = "idUsuario"),
        @Result(column = "nombre_usuario", property = "username"),
        @Result(column = "password_hash", property = "password"),
        @Result(column = "activo", property = "activo"),
        @Result(column = "roles", property = "roles")
    })
    @Select("""
        SELECT u.id_usuario, u.nombre_usuario, u.password_hash,
               (u.estado = 'ACTIVO') AS activo,
               CONCAT('ROLE_', r.nombre) AS roles
        FROM usuario u
        JOIN rol r ON r.id_rol = u.id_rol
        WHERE u.nombre_usuario = #{username} AND u.estado_registro = 'ACTIVO'
        """)
    UsuarioLoginDTO obtenerUsuarioLogin(@Param("username") String username);

    @Results(id = "mapRefreshToken", value = {
        @Result(column = "id_token_refresco", property = "idTokenRefresco"),
        @Result(column = "id_usuario", property = "idUsuario"),
        @Result(column = "nombre_usuario", property = "username"),
        @Result(column = "token", property = "token"),
        @Result(column = "fecha_expiracion", property = "fechaExpiracion")
    })
    @Select("""
        SELECT rt.id_token_refresco, rt.id_usuario, u.nombre_usuario, rt.token, rt.fecha_expiracion
        FROM refresh_token rt
        JOIN usuario u ON u.id_usuario = rt.id_usuario
        WHERE rt.token = #{token}
        """)
    RefreshTokenDTO obtenerTokenUsuario(@Param("token") String token);

    @Insert("INSERT INTO refresh_token (id_usuario, token, fecha_expiracion) VALUES (#{idUsuario}, #{token}, #{fechaExpiracion})")
    void insertarToken(RefreshTokenDTO refreshToken);

    @Delete("DELETE FROM refresh_token WHERE token = #{token}")
    void eliminarToken(@Param("token") String token);

    // ------------------------------------------------------------------
    // CRUD de usuarios del sistema
    // ------------------------------------------------------------------

    @Results(id = "mapListResultUsuario", value = {
        @Result(column = "id_usuario", property = "idUsuario"),
        @Result(column = "id_rol", property = "idRol"),
        @Result(column = "nombre_rol", property = "nombreRol"),
        @Result(column = "nombre_usuario", property = "nombreUsuario"),
        @Result(column = "nombre_completo", property = "nombreCompleto"),
        @Result(column = "email", property = "email"),
        @Result(column = "estado", property = "estado")
    })
    @Select("""
        SELECT u.id_usuario, u.id_rol, r.nombre AS nombre_rol, u.nombre_usuario, u.nombre_completo,
               u.email, u.estado
        FROM usuario u
        JOIN rol r ON r.id_rol = u.id_rol
        WHERE u.estado_registro = 'ACTIVO'
        ORDER BY u.nombre_usuario
        """)
    List<UsuarioDTO> listarUsuarios();

    // idAuditoria = quien ejecuta la accion (viene de AuditContext), no confundir
    // con args.idUsuario, que es el usuario que se esta creando/editando.
    @Options(useGeneratedKeys = true, keyProperty = "args.idUsuario")
    @Insert("INSERT INTO usuario (id_rol, nombre_usuario, password_hash, nombre_completo, email, estado, "
            + "usuario_creacion, ip_creacion) "
            + "VALUES (#{args.idRol}, #{args.nombreUsuario}, #{passwordHash}, #{args.nombreCompleto}, "
            + "#{args.email}, #{args.estado}, #{idAuditoria}, #{ip})")
    int insertarUsuario(@Param("args") UsuarioRequest args, @Param("passwordHash") String passwordHash,
                        @Param("idAuditoria") Integer idAuditoria, @Param("ip") String ip);

    // No actualiza nombre_usuario (identifica al usuario) ni password_hash
    // (eso lo maneja actualizarPassword, por separado).
    @Update("UPDATE usuario SET id_rol = #{args.idRol}, nombre_completo = #{args.nombreCompleto}, "
            + "email = #{args.email}, estado = #{args.estado}, usuario_actualizacion = #{idAuditoria}, "
            + "fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_usuario = #{args.idUsuario} AND estado_registro = 'ACTIVO'")
    int actualizarUsuario(@Param("args") UsuarioRequest args, @Param("idAuditoria") Integer idAuditoria, @Param("ip") String ip);

    @Update("UPDATE usuario SET password_hash = #{passwordHash}, usuario_actualizacion = #{idAuditoria}, "
            + "fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_usuario = #{idUsuario} AND estado_registro = 'ACTIVO'")
    int actualizarPassword(@Param("idUsuario") Integer idUsuario, @Param("passwordHash") String passwordHash,
                           @Param("idAuditoria") Integer idAuditoria, @Param("ip") String ip);

    @Select("SELECT password_hash FROM usuario WHERE id_usuario = #{idUsuario} AND estado_registro = 'ACTIVO'")
    String obtenerPasswordHash(@Param("idUsuario") Integer idUsuario);

    @Update("UPDATE usuario SET estado_registro = 'ELIMINADO', usuario_eliminacion = #{idAuditoria}, "
            + "fecha_eliminacion = NOW(), ip_eliminacion = #{ip} WHERE id_usuario = #{idUsuario} AND estado_registro = 'ACTIVO'")
    int eliminarUsuario(@Param("idUsuario") Integer idUsuario, @Param("idAuditoria") Integer idAuditoria, @Param("ip") String ip);

    // ------------------------------------------------------------------
    // Resguardo: el sistema nunca puede quedarse sin un Administrador activo
    // (ver ImplUsuario.insertUpdateUsuario / eliminarUsuario).
    // ------------------------------------------------------------------

    @Select("SELECT COUNT(*) FROM usuario u JOIN rol r ON r.id_rol = u.id_rol "
            + "WHERE r.nombre = 'ADMINISTRADOR' AND u.estado = 'ACTIVO' AND u.estado_registro = 'ACTIVO' "
            + "AND u.id_usuario <> #{idUsuario}")
    int contarAdministradoresActivosDistintosDe(@Param("idUsuario") Integer idUsuario);

    @Select("SELECT COUNT(*) > 0 FROM rol WHERE id_rol = #{idRol} AND nombre = 'ADMINISTRADOR'")
    boolean esRolAdministrador(@Param("idRol") Integer idRol);

    @Select("SELECT COUNT(*) > 0 FROM usuario u JOIN rol r ON r.id_rol = u.id_rol "
            + "WHERE u.id_usuario = #{idUsuario} AND r.nombre = 'ADMINISTRADOR' "
            + "AND u.estado = 'ACTIVO' AND u.estado_registro = 'ACTIVO'")
    boolean esAdministradorActivo(@Param("idUsuario") Integer idUsuario);

    // ------------------------------------------------------------------
    // MFA (verificacion en dos pasos, TOTP) -- ver ImplUsuario.login /
    // verificarMfa / activarMiMfa / confirmarMiMfa / desactivarMiMfa.
    // ------------------------------------------------------------------

    @Select("SELECT nombre_usuario FROM usuario WHERE id_usuario = #{idUsuario} AND estado_registro = 'ACTIVO'")
    String obtenerNombreUsuarioPorId(@Param("idUsuario") Integer idUsuario);

    @Results(id = "mapEstadoMfaInterno", value = {
        @Result(column = "mfa_habilitado", property = "mfaHabilitado"),
        @Result(column = "mfa_secret", property = "mfaSecret"),
        @Result(column = "mfa_confirmado", property = "mfaConfirmado")
    })
    @Select("SELECT mfa_habilitado, mfa_secret, mfa_confirmado FROM usuario "
            + "WHERE id_usuario = #{idUsuario} AND estado_registro = 'ACTIVO'")
    EstadoMfaInternoDTO obtenerEstadoMfaInterno(@Param("idUsuario") Integer idUsuario);

    // Se usa tanto para la activacion forzada en el login como para la que el
    // usuario dispara el mismo desde su perfil: siempre deja mfa_confirmado
    // en 0, asi que si alguien abandona a medio camino, la proxima vez que
    // pase por aca (login o "activar" de nuevo) se genera un secreto nuevo
    // y se le vuelve a mostrar el QR, en vez de dejarlo pedido un codigo de
    // un secreto que nunca llego a guardar en su app.
    @Update("UPDATE usuario SET mfa_habilitado = 1, mfa_secret = #{secreto}, mfa_confirmado = 0, "
            + "usuario_actualizacion = #{idAuditoria}, fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_usuario = #{idUsuario} AND estado_registro = 'ACTIVO'")
    int activarMfa(@Param("idUsuario") Integer idUsuario, @Param("secreto") String secreto,
                   @Param("idAuditoria") Integer idAuditoria, @Param("ip") String ip);

    @Update("UPDATE usuario SET mfa_confirmado = 1, usuario_actualizacion = #{idAuditoria}, "
            + "fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_usuario = #{idUsuario} AND estado_registro = 'ACTIVO'")
    int confirmarMfa(@Param("idUsuario") Integer idUsuario, @Param("idAuditoria") Integer idAuditoria, @Param("ip") String ip);

    @Update("UPDATE usuario SET mfa_habilitado = 0, mfa_secret = NULL, mfa_confirmado = 0, "
            + "usuario_actualizacion = #{idAuditoria}, fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_usuario = #{idUsuario} AND estado_registro = 'ACTIVO'")
    int desactivarMfa(@Param("idUsuario") Integer idUsuario, @Param("idAuditoria") Integer idAuditoria, @Param("ip") String ip);

    // Modulos que el usuario puede ver (y con que nivel: LECTURA o
    // ESCRITURA), segun el rol_permiso de SU rol. Lo consume tanto
    // ControllerUsuario.obtenerMisPermisos (menu/rutas del frontend) como
    // PermisoAccessFilter (que rechaza de verdad los POST de escritura si el
    // nivel es LECTURA, para que no sea una restriccion solo cosmetica).
    @Results(id = "mapPermisoUsuario", value = {
        @Result(column = "codigo", property = "codigo"),
        @Result(column = "nivel_acceso", property = "nivelAcceso")
    })
    @Select("SELECT m.codigo, rp.nivel_acceso FROM usuario u "
            + "JOIN rol_permiso rp ON rp.id_rol = u.id_rol "
            + "JOIN modulo_sistema m ON m.id_modulo = rp.id_modulo "
            + "WHERE u.id_usuario = #{idUsuario} AND u.estado_registro = 'ACTIVO' "
            + "ORDER BY m.orden")
    List<PermisoAsignadoDTO> listarPermisosDeUsuario(@Param("idUsuario") Integer idUsuario);

    // A diferencia de desactivarMfa, esto NO apaga mfa_habilitado: es para
    // cuando un usuario perdio el telefono con su app de autenticacion y no
    // puede entrar (el autoservicio de arriba es inutil ahi, porque exige un
    // codigo valido que ya no puede generar). El administrador borra el
    // secreto y deja mfa_confirmado en 0 sin tocar mfa_habilitado: si seguia
    // obligado a usar MFA, lo sigue estando, solo que en su proximo login el
    // sistema entra por la rama "aun no confirmado" y le genera un QR nuevo
    // para que lo configure de cero en su telefono (o en uno nuevo).
    @Update("UPDATE usuario SET mfa_secret = NULL, mfa_confirmado = 0, "
            + "usuario_actualizacion = #{idAuditoria}, fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_usuario = #{idUsuario} AND estado_registro = 'ACTIVO'")
    int resetearMfa(@Param("idUsuario") Integer idUsuario, @Param("idAuditoria") Integer idAuditoria, @Param("ip") String ip);
}
