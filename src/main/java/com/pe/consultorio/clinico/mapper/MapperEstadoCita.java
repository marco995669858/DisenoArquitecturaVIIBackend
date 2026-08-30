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
import com.pe.consultorio.clinico.model.estadoCita.request.EstadoCitaRequest;
import com.pe.consultorio.clinico.model.estadoCita.response.EstadoCitaDTO;

@Mapper
public interface MapperEstadoCita {

    @Results(id = "mapListResultEstadoCita", value = {
        @Result(column = "IDESTADOCITA", property = "idEstadoCita"),
        @Result(column = "NOMBRE", property = "nombre"),
        @Result(column = "COLORHEX", property = "colorHex")
    })
    @Select(value = "{CALL CONSULTORIOCLINICO.PKGESTADOCITA.USPESTADOCITALISTAR(#{data, mode=OUT, jdbcType=CURSOR, javaType=java.sql.ResultSet, resultMap=mapListResultEstadoCita})}")
    @Options(statementType = StatementType.CALLABLE)
    List<EstadoCitaDTO> listarEstadoCitas(ResultCustomPage<EstadoCitaDTO> data);

    @Update("{CALL CONSULTORIOCLINICO.PKGESTADOCITA.USPREGISTRARACTUALIZARESTADOCITA("
            + "#{data.idEstadoCita, mode=IN, jdbcType=INTEGER}, "
            + "#{data.nombre, mode=IN, jdbcType=VARCHAR}, "
            + "#{data.colorHex, mode=IN, jdbcType=CHAR}, "
            + "#{data.usuario, mode=IN, jdbcType=VARCHAR}, "
            + "#{estadoRespuesta, mode=OUT, jdbcType=INTEGER}, "
            + "#{detalleRespuesta, mode=OUT, jdbcType=VARCHAR}"
            + ")}")
    @Options(statementType = StatementType.CALLABLE)
    void insertUpdateEstadoCita(ResultCustomRUD<EstadoCitaRequest> args);

    @Update("{CALL CONSULTORIOCLINICO.PKGESTADOCITA.USPELIMINARESTADOCITA("
            + "#{data.idEstadoCita, mode=IN, jdbcType=INTEGER}, "
            + "#{data.usuario, mode=IN, jdbcType=VARCHAR}, "
            + "#{estadoRespuesta, mode=OUT, jdbcType=INTEGER}, "
            + "#{detalleRespuesta, mode=OUT, jdbcType=VARCHAR}"
            + ")}")
    @Options(statementType = StatementType.CALLABLE)
    void eliminarEstadoCita(ResultCustomRUD<EstadoCitaRequest> args);
}
