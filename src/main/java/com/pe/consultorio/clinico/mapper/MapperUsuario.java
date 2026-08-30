package com.pe.consultorio.clinico.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.StatementType;

import com.pe.consultorio.clinico.model.ResultCustomPage;
import com.pe.consultorio.clinico.model.ResultCustomRUD;
import com.pe.consultorio.clinico.model.usuario.request.TokenRequest;
import com.pe.consultorio.clinico.model.usuario.response.RefreshTokenDTO;
import com.pe.consultorio.clinico.model.usuario.response.UsuarioLoginDTO;

@Mapper
public interface MapperUsuario {

    @Results(id = "mapListResultUsuarioLogin", value = {
        @Result(column = "IDUSUARIO", property = "idUsuario"),
        @Result(column = "USERNAME", property = "username"),
        @Result(column = "PASSWORD", property = "password"),
        @Result(column = "ACTIVO", property = "activo"),
        @Result(column = "IDEMPRESA", property = "idEmpresa"),
        @Result(column = "TIPONEGOCIO", property = "tipoNegocio"),
        @Result(column = "ROLES", property = "roles")
    })
    @Select(value = "{CALL CONSULTORIOCLINICO.PKGUSUARIOS.USPOBTENERUSUARIOLOGIN("
            + "#{username, mode=IN, jdbcType=VARCHAR}, "
            + "#{data.data, mode=OUT, jdbcType=CURSOR, javaType=java.sql.ResultSet, resultMap=mapListResultUsuarioLogin}"
            + ")}")
    @Options(statementType = StatementType.CALLABLE)
    List<UsuarioLoginDTO> obtenerUsuarioLogin(String username,ResultCustomPage<UsuarioLoginDTO> data);

    @Results(id = "mapListResultTokenUsuario", value = {
        @Result(column = "IDTOKENREFRESCO", property = "idTokenRefresco"),
        @Result(column = "IDUSUARIO", property = "idUsuario"),
        @Result(column = "USERNAME", property = "username"),
        @Result(column = "TOKEN", property = "token"),
        @Result(column = "FECHAEXPIRACION", property = "fechaExpiracion"),
        @Result(column = "IDEMPRESA", property = "idEmpresa"),
    })
    @Select(value = "{CALL CONSULTORIOCLINICO.PKGUSUARIOS.USPOBTENERTOKEN("
            + "#{token, mode=IN, jdbcType=VARCHAR}, "
            + "#{data.data, mode=OUT, jdbcType=CURSOR, javaType=java.sql.ResultSet, resultMap=mapListResultTokenUsuario}"
            + ")}")
    @Options(statementType = StatementType.CALLABLE)
    List<RefreshTokenDTO> obtenerTokenUsuario(String token,ResultCustomPage<RefreshTokenDTO> data);

    @Update("{CALL CONSULTORIOCLINICO.PKGUSUARIOS.USPINSERTARTOKEN("
            + "#{data.idUsuario, mode=IN, jdbcType=INTEGER}, "
            + "#{data.fechaExpiracion, mode=IN, jdbcType=DATE}, "
            + "#{data.usuario, mode=IN, jdbcType=VARCHAR}, "
            + "#{estadoRespuesta, mode=OUT, jdbcType=INTEGER}, "
            + "#{detalleRespuesta, mode=OUT, jdbcType=VARCHAR}"
            + ")}")
    @Options(statementType = StatementType.CALLABLE)
    void insertarToken(ResultCustomRUD<TokenRequest> args);

    @Update("{CALL CONSULTORIOCLINICO.PKGUSUARIOS.USPELIMINARTOKENSUSUARIO("
            + "#{data.idUsuario, mode=IN, jdbcType=INTEGER}, "
            + "#{data.usuario, mode=IN, jdbcType=VARCHAR}, "
            + "#{estadoRespuesta, mode=OUT, jdbcType=INTEGER}, "
            + "#{detalleRespuesta, mode=OUT, jdbcType=VARCHAR}"
            + ")}")
    @Options(statementType = StatementType.CALLABLE)
    void eliminarToken(ResultCustomRUD<TokenRequest> args);
}
