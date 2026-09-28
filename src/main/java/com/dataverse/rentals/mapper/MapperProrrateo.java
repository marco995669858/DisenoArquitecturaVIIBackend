package com.dataverse.rentals.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.dataverse.rentals.model.prorrateo.request.ProrrateoRequest;
import com.dataverse.rentals.model.prorrateo.response.ConsumoUnidadDTO;
import com.dataverse.rentals.model.prorrateo.response.ProrrateoDTO;

@Mapper
public interface MapperProrrateo {

    @Results(id = "mapListResultProrrateo", value = {
        @Result(column = "id_prorrateo", property = "idProrrateo"),
        @Result(column = "id_unidad", property = "idUnidad"),
        @Result(column = "numero_unidad", property = "numeroUnidad"),
        @Result(column = "id_recibo", property = "idRecibo"),
        @Result(column = "periodo", property = "periodo"),
        @Result(column = "tipo_servicio", property = "tipoServicio"),
        @Result(column = "consumo_individual", property = "consumoIndividual"),
        @Result(column = "consumo_comun_asignado", property = "consumoComunAsignado"),
        @Result(column = "costo_unitario", property = "costoUnitario"),
        @Result(column = "monto_calculado", property = "montoCalculado")
    })
    @Select("""
        SELECT p.id_prorrateo, p.id_unidad, u.numero_unidad, p.id_recibo, p.periodo, p.tipo_servicio,
               p.consumo_individual, p.consumo_comun_asignado, p.costo_unitario, p.monto_calculado
        FROM prorrateo p
        JOIN unidad u ON u.id_unidad = p.id_unidad
        WHERE p.estado_registro = 'ACTIVO'
        ORDER BY p.periodo DESC, u.numero_unidad
        """)
    List<ProrrateoDTO> listarProrrateos();

    @Options(useGeneratedKeys = true, keyProperty = "args.idProrrateo")
    @Insert("INSERT INTO prorrateo (id_unidad, id_recibo, periodo, tipo_servicio, consumo_individual, "
            + "consumo_comun_asignado, costo_unitario, monto_calculado, usuario_creacion, ip_creacion) "
            + "VALUES (#{args.idUnidad}, #{args.idRecibo}, #{args.periodo}, #{args.tipoServicio}, "
            + "#{args.consumoIndividual}, #{args.consumoComunAsignado}, #{args.costoUnitario}, "
            + "#{args.montoCalculado}, #{idUsuario}, #{ip})")
    int insertarProrrateo(@Param("args") ProrrateoRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE prorrateo SET id_unidad = #{args.idUnidad}, id_recibo = #{args.idRecibo}, "
            + "periodo = #{args.periodo}, "
            + "tipo_servicio = #{args.tipoServicio}, consumo_individual = #{args.consumoIndividual}, "
            + "consumo_comun_asignado = #{args.consumoComunAsignado}, costo_unitario = #{args.costoUnitario}, "
            + "monto_calculado = #{args.montoCalculado}, usuario_actualizacion = #{idUsuario}, "
            + "fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_prorrateo = #{args.idProrrateo} AND estado_registro = 'ACTIVO'")
    int actualizarProrrateo(@Param("args") ProrrateoRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE prorrateo SET estado_registro = 'ELIMINADO', usuario_eliminacion = #{idUsuario}, "
            + "fecha_eliminacion = NOW(), ip_eliminacion = #{ip} WHERE id_prorrateo = #{idProrrateo} AND estado_registro = 'ACTIVO'")
    int eliminarProrrateo(@Param("idProrrateo") Integer idProrrateo, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    // ------------------------------------------------------------------
    // Soporte para CU-05 -- Calcular Prorrateo (calculo automatico)
    // ------------------------------------------------------------------

    @Select("SELECT COUNT(DISTINCT m.id_unidad) FROM medidor m "
            + "WHERE m.tipo_servicio = #{tipoServicio} AND m.tipo_medidor = 'INDIVIDUAL' AND m.estado_registro = 'ACTIVO'")
    int contarUnidadesConMedidorIndividual(@Param("tipoServicio") String tipoServicio);

    @Select("SELECT COUNT(DISTINCT m.id_unidad) FROM medidor m "
            + "JOIN lectura_medidor lm ON lm.id_medidor = m.id_medidor "
            + "WHERE m.tipo_servicio = #{tipoServicio} AND m.tipo_medidor = 'INDIVIDUAL' AND m.estado_registro = 'ACTIVO' "
            + "AND lm.periodo = #{periodo} AND lm.estado_registro = 'ACTIVO'")
    int contarUnidadesConLecturaIndividual(@Param("periodo") String periodo, @Param("tipoServicio") String tipoServicio);

    @Select("SELECT COALESCE(SUM(lm.lectura_actual - lm.lectura_anterior), -1) FROM lectura_medidor lm "
            + "JOIN medidor m ON m.id_medidor = lm.id_medidor "
            + "WHERE m.tipo_servicio = #{tipoServicio} AND m.tipo_medidor = 'GENERAL' AND m.estado_registro = 'ACTIVO' "
            + "AND lm.periodo = #{periodo} AND lm.estado_registro = 'ACTIVO'")
    BigDecimal obtenerConsumoGeneral(@Param("periodo") String periodo, @Param("tipoServicio") String tipoServicio);

    @Results(id = "mapListResultConsumoUnidad", value = {
        @Result(column = "id_unidad", property = "idUnidad"),
        @Result(column = "numero_unidad", property = "numeroUnidad"),
        @Result(column = "area_m2", property = "areaM2"),
        @Result(column = "consumo_individual", property = "consumoIndividual")
    })
    @Select("SELECT u.id_unidad, u.numero_unidad, u.area_m2, "
            + "(lm.lectura_actual - lm.lectura_anterior) AS consumo_individual "
            + "FROM lectura_medidor lm "
            + "JOIN medidor m ON m.id_medidor = lm.id_medidor "
            + "JOIN unidad u ON u.id_unidad = m.id_unidad "
            + "WHERE m.tipo_servicio = #{tipoServicio} AND m.tipo_medidor = 'INDIVIDUAL' "
            + "AND m.estado_registro = 'ACTIVO' AND u.estado_registro = 'ACTIVO' "
            + "AND lm.periodo = #{periodo} AND lm.estado_registro = 'ACTIVO' "
            + "ORDER BY u.numero_unidad")
    List<ConsumoUnidadDTO> listarConsumosIndividuales(@Param("periodo") String periodo, @Param("tipoServicio") String tipoServicio);

    @Select("SELECT monto_total FROM recibo_servicio "
            + "WHERE periodo = #{periodo} AND tipo_servicio = #{tipoServicio} AND estado_registro = 'ACTIVO'")
    BigDecimal obtenerMontoTotalRecibo(@Param("periodo") String periodo, @Param("tipoServicio") String tipoServicio);

    @Select("SELECT id_recibo FROM recibo_servicio "
            + "WHERE periodo = #{periodo} AND tipo_servicio = #{tipoServicio} AND estado_registro = 'ACTIVO'")
    Integer obtenerIdRecibo(@Param("periodo") String periodo, @Param("tipoServicio") String tipoServicio);

    @Select("SELECT id_prorrateo FROM prorrateo WHERE id_unidad = #{idUnidad} AND periodo = #{periodo} "
            + "AND tipo_servicio = #{tipoServicio} AND estado_registro = 'ACTIVO'")
    Integer buscarIdProrrateoExistente(@Param("idUnidad") Integer idUnidad, @Param("periodo") String periodo,
                                       @Param("tipoServicio") String tipoServicio);

    // Usado por CU-06 -- Generar Liquidacion para tomar el monto de luz/agua
    // ya calculado por CU-05, sin repetir la formula del prorrateo.
    @Select("SELECT monto_calculado FROM prorrateo WHERE id_unidad = #{idUnidad} AND periodo = #{periodo} "
            + "AND tipo_servicio = #{tipoServicio} AND estado_registro = 'ACTIVO'")
    BigDecimal obtenerMontoCalculado(@Param("idUnidad") Integer idUnidad, @Param("periodo") String periodo,
                                     @Param("tipoServicio") String tipoServicio);
}
