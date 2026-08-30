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
import com.pe.consultorio.clinico.model.especialidad.request.EspecialidadRequest;
import com.pe.consultorio.clinico.model.especialidad.response.EspecialidadDTO;

@Mapper
public interface MapperEspecialidad {

    @Results(id = "mapListResultEspecialidad", value = {
        @Result(column = "IDESPECIALIDAD", property = "idEspecialidad"),
        @Result(column = "NOMBRE", property = "nombre"),
        @Result(column = "DESCRIPCION", property = "descripcion")
    })
    @Select(value = "{CALL CONSULTORIOCLINICO.PKGESPECIALIDAD.USPESPECIALIDADLISTAR(#{data, mode=OUT, jdbcType=CURSOR, javaType=java.sql.ResultSet, resultMap=mapListResultEspecialidad})}")
    @Options(statementType = StatementType.CALLABLE)
    List<EspecialidadDTO> listarEspecialidades(ResultCustomPage<EspecialidadDTO> data);

    @Update("{CALL CONSULTORIOCLINICO.PKGESPECIALIDAD.USPREGISTRARACTUALIZARESPECIALIDADES("
            + "#{data.idEspecialidad, mode=IN, jdbcType=INTEGER}, "
            + "#{data.nombre, mode=IN, jdbcType=VARCHAR}, "
            + "#{data.descripcion, mode=IN, jdbcType=VARCHAR}, "
            + "#{data.usuario, mode=IN, jdbcType=VARCHAR}, "
            + "#{estadoRespuesta, mode=OUT, jdbcType=INTEGER}, "
            + "#{detalleRespuesta, mode=OUT, jdbcType=VARCHAR}"
            + ")}")
    @Options(statementType = StatementType.CALLABLE)
    void registrarActualizarEspecialidad(ResultCustomRUD<EspecialidadRequest> args);

    @Update("{CALL CONSULTORIOCLINICO.PKGESPECIALIDAD.USPELIMINARESPECIALIDAD("
            + "#{data.idEspecialidad, mode=IN, jdbcType=INTEGER}, "
            + "#{data.usuario, mode=IN, jdbcType=VARCHAR}, "
            + "#{estadoRespuesta, mode=OUT, jdbcType=INTEGER}, "
            + "#{detalleRespuesta, mode=OUT, jdbcType=VARCHAR}"
            + ")}")
    @Options(statementType = StatementType.CALLABLE)
    void eliminarEspecialidad(ResultCustomRUD<EspecialidadRequest> args);
}
