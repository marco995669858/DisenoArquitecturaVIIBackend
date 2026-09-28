package com.dataverse.rentals.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.dataverse.rentals.model.modulo.response.ModuloSistemaDTO;

@Mapper
public interface MapperModuloSistema {

    @Results(id = "mapListResultModulo", value = {
        @Result(column = "id_modulo", property = "idModulo"),
        @Result(column = "codigo", property = "codigo"),
        @Result(column = "nombre", property = "nombre")
    })
    @Select("SELECT id_modulo, codigo, nombre FROM modulo_sistema ORDER BY orden")
    List<ModuloSistemaDTO> listarModulos();
}
