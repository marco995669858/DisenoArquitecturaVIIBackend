package com.pe.consultorio.clinico.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;

@Mapper
public interface MapperAseguradora {

    @Select(value = "{CALL MONITOREOIMA.PKG_MONITOREO_INFORME.SP_AGREGAR_ALFRESCO( "
                        + "#{txNombreArchivo, jdbcType=VARCHAR, mode=IN},"
                        + "#{txRutaAlfresco, jdbcType=VARCHAR, mode=IN}, " 
                        + "#{txUiid, jdbcType=VARCHAR, mode=IN}," 
                        + "#{txNombreArchivoOriginal, jdbcType=VARCHAR, mode=IN},"
                        + "#{txUsuario, jdbcType=VARCHAR, mode=IN},"
                        + "#{idArchivo, jdbcType=INTEGER, mode=OUT},"
                        + "#{respuesta, jdbcType=INTEGER, mode=OUT}) }")
   @Options(statementType = StatementType.CALLABLE)
   void agregarAlfresco();

}
