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
import com.pe.consultorio.clinico.model.tipoConsullta.request.TipoConsultaRequest;
import com.pe.consultorio.clinico.model.tipoConsullta.response.TipoConsultaDTO;

@Mapper
public interface MapperTipoConsulta {

    @Results(id = "mapListResultTipoConsulta", value = {
        @Result(column = "IDTIPODOCUMENTO", property = "idTipoDocumento"),
        @Result(column = "NOMBRE", property = "nombre"),
        @Result(column = "DURACIONESTIMADAMINUTOS", property = "duracionEstimadaMinutos"),
        @Result(column = "PRECIOBASE", property = "precioBase")
    })
    @Select(value = "{CALL CONSULTORIOCLINICO.PKGTIPOCONSULTA.USPTIPOCONSULTALISTAR(#{data, mode=OUT, jdbcType=CURSOR, javaType=java.sql.ResultSet, resultMap=mapListResultTipoConsulta})}")
    @Options(statementType = StatementType.CALLABLE)
    List<TipoConsultaDTO> listarTipoConsultas(ResultCustomPage<TipoConsultaDTO> data);

    @Update("{CALL CONSULTORIOCLINICO.PKGTIPOCONSULTA.USPREGISTRARACTUALIZARTIPOCONSULTA("
            + "#{data.idTipoConsulta, mode=IN, jdbcType=INTEGER}, "
            + "#{data.nombre, mode=IN, jdbcType=VARCHAR}, "
            + "#{data.duracionEstimadaMinutos, mode=IN, jdbcType=INTEGER}, "
            + "#{data.precioBase, mode=IN, jdbcType=INTEGER}, "
            + "#{data.usuario, mode=IN, jdbcType=VARCHAR}, "
            + "#{estadoRespuesta, mode=OUT, jdbcType=INTEGER}, "
            + "#{detalleRespuesta, mode=OUT, jdbcType=VARCHAR}"
            + ")}")
    @Options(statementType = StatementType.CALLABLE)
    void insertUpdateTipoConsulta(ResultCustomRUD<TipoConsultaRequest> args);

    @Update("{CALL CONSULTORIOCLINICO.PKGTIPOCONSULTA.USPELIMINARTIPOCONSULTA("
            + "#{data.idTipoConsulta, mode=IN, jdbcType=INTEGER}, "
            + "#{data.usuario, mode=IN, jdbcType=VARCHAR}, "
            + "#{estadoRespuesta, mode=OUT, jdbcType=INTEGER}, "
            + "#{detalleRespuesta, mode=OUT, jdbcType=VARCHAR}"
            + ")}")
    @Options(statementType = StatementType.CALLABLE)
    void eliminarTipoConsulta(ResultCustomRUD<TipoConsultaRequest> args);
}
