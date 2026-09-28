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

import com.dataverse.rentals.model.periodoFacturacion.request.PeriodoFacturacionRequest;
import com.dataverse.rentals.model.periodoFacturacion.response.PeriodoFacturacionDTO;

@Mapper
public interface MapperPeriodoFacturacion {

    @Results(id = "mapListResultPeriodoFacturacion", value = {
        @Result(column = "id_periodo", property = "idPeriodo"),
        @Result(column = "periodo", property = "periodo"),
        @Result(column = "estado", property = "estado")
    })
    @Select("SELECT id_periodo, periodo, estado FROM periodo_facturacion ORDER BY periodo DESC")
    List<PeriodoFacturacionDTO> listarPeriodos();

    @Options(useGeneratedKeys = true, keyProperty = "args.idPeriodo")
    @Insert("INSERT INTO periodo_facturacion (periodo, estado, usuario_creacion) "
            + "VALUES (#{args.periodo}, #{args.estado}, #{idUsuario})")
    int insertarPeriodo(@Param("args") PeriodoFacturacionRequest args, @Param("idUsuario") Integer idUsuario);

    @Update("UPDATE periodo_facturacion SET estado = #{args.estado} WHERE id_periodo = #{args.idPeriodo}")
    int actualizarPeriodo(@Param("args") PeriodoFacturacionRequest args);

    /**
     * Crea el periodo si todavia no existe, en EN_PROGRESO. Es idempotente
     * (INSERT IGNORE): si ya existe, no hace nada. La usan las demas
     * entidades que referencian periodo_facturacion(periodo) -- lectura de
     * medidores, recibos, prorrateo y liquidacion -- antes de insertar, para
     * no depender de que alguien haya dado de alta el periodo a mano desde
     * otra pantalla.
     */
    @Insert("INSERT IGNORE INTO periodo_facturacion (periodo, usuario_creacion) VALUES (#{periodo}, #{idUsuario})")
    int asegurarPeriodo(@Param("periodo") String periodo, @Param("idUsuario") Integer idUsuario);

    // Usado por CU-06 al terminar de generar las liquidaciones de un periodo.
    @Update("UPDATE periodo_facturacion SET estado = #{estado} WHERE periodo = #{periodo}")
    int actualizarEstadoPorPeriodo(@Param("periodo") String periodo, @Param("estado") String estado);
}
