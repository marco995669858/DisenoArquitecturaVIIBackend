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
import com.pe.consultorio.clinico.model.tipoDocumento.request.TipoDocumentoRequest;
import com.pe.consultorio.clinico.model.tipoDocumento.response.TipoDocumentoDTO;

@Mapper
public interface MapperTipoDocumento {

    @Results(id = "mapListResultTipoDocumento", value = {
        @Result(column = "IDTIPODOCUMENTO", property = "idTipoDocumento"),
        @Result(column = "NOMBRE", property = "nombre"),
        @Result(column = "ABREVIATURA", property = "abreviatura")
    })
    @Select(value = "{CALL CONSULTORIOCLINICO.PKGTIPODOCUMENTO.USPTIPODOCUMENTOLISTAR(#{data, mode=OUT, jdbcType=CURSOR, javaType=java.sql.ResultSet, resultMap=mapListResultTipoDocumento})}")
    @Options(statementType = StatementType.CALLABLE)
    List<TipoDocumentoDTO> listarTipoDocumentos(ResultCustomPage<TipoDocumentoDTO> data);

    @Update("{CALL CONSULTORIOCLINICO.PKGTIPODOCUMENTO.USPREGISTRARACTUALIZARTIPODOCUMENTO("
            + "#{data.idTipoDocumento, mode=IN, jdbcType=INTEGER}, "
            + "#{data.nombre, mode=IN, jdbcType=VARCHAR}, "
            + "#{data.abreviatura, mode=IN, jdbcType=CHAR}, "
            + "#{data.usuario, mode=IN, jdbcType=VARCHAR}, "
            + "#{estadoRespuesta, mode=OUT, jdbcType=INTEGER}, "
            + "#{detalleRespuesta, mode=OUT, jdbcType=VARCHAR}"
            + ")}")
    @Options(statementType = StatementType.CALLABLE)
    void insertUpdateTipoDocumento(ResultCustomRUD<TipoDocumentoRequest> args);

    @Update("{CALL CONSULTORIOCLINICO.PKGTIPODOCUMENTO.USPELIMINARTIPODOCUMENTO("
            + "#{data.idTipoDocumento, mode=IN, jdbcType=INTEGER}, "
            + "#{data.usuario, mode=IN, jdbcType=VARCHAR}, "
            + "#{estadoRespuesta, mode=OUT, jdbcType=INTEGER}, "
            + "#{detalleRespuesta, mode=OUT, jdbcType=VARCHAR}"
            + ")}")
    @Options(statementType = StatementType.CALLABLE)
    void eliminarTipoDocumento(ResultCustomRUD<TipoDocumentoRequest> args);
}
