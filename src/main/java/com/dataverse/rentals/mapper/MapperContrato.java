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

import com.dataverse.rentals.model.contrato.request.ContratoRequest;
import com.dataverse.rentals.model.contrato.response.ContratoDTO;
import com.dataverse.rentals.model.contrato.response.ContratoPorVencerDTO;

@Mapper
public interface MapperContrato {

    @Results(id = "mapListResultContrato", value = {
        @Result(column = "id_contrato", property = "idContrato"),
        @Result(column = "id_unidad", property = "idUnidad"),
        @Result(column = "numero_unidad", property = "numeroUnidad"),
        @Result(column = "id_inquilino", property = "idInquilino"),
        @Result(column = "nombre_inquilino", property = "nombreInquilino"),
        @Result(column = "fecha_inicio", property = "fechaInicio"),
        @Result(column = "fecha_fin", property = "fechaFin"),
        @Result(column = "monto_alquiler", property = "montoAlquiler"),
        @Result(column = "ruta_contrato_digital", property = "rutaContratoDigital"),
        @Result(column = "estado", property = "estado")
    })
    @Select("""
        SELECT c.id_contrato, c.id_unidad, u.numero_unidad, c.id_inquilino, i.nombre_completo AS nombre_inquilino,
               c.fecha_inicio, c.fecha_fin, c.monto_alquiler, c.ruta_contrato_digital, c.estado
        FROM contrato c
        JOIN unidad u ON u.id_unidad = c.id_unidad
        JOIN inquilino i ON i.id_inquilino = c.id_inquilino
        WHERE c.estado_registro = 'ACTIVO'
        ORDER BY c.fecha_inicio DESC
        """)
    List<ContratoDTO> listarContratos();

    @Options(useGeneratedKeys = true, keyProperty = "args.idContrato")
    @Insert("INSERT INTO contrato (id_unidad, id_inquilino, fecha_inicio, fecha_fin, monto_alquiler, "
            + "ruta_contrato_digital, estado, usuario_creacion, ip_creacion) "
            + "VALUES (#{args.idUnidad}, #{args.idInquilino}, #{args.fechaInicio}, #{args.fechaFin}, "
            + "#{args.montoAlquiler}, #{args.rutaContratoDigital}, #{args.estado}, #{idUsuario}, #{ip})")
    int insertarContrato(@Param("args") ContratoRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE contrato SET id_unidad = #{args.idUnidad}, id_inquilino = #{args.idInquilino}, "
            + "fecha_inicio = #{args.fechaInicio}, fecha_fin = #{args.fechaFin}, monto_alquiler = #{args.montoAlquiler}, "
            + "ruta_contrato_digital = #{args.rutaContratoDigital}, estado = #{args.estado}, "
            + "usuario_actualizacion = #{idUsuario}, fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_contrato = #{args.idContrato} AND estado_registro = 'ACTIVO'")
    int actualizarContrato(@Param("args") ContratoRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE contrato SET estado_registro = 'ELIMINADO', usuario_eliminacion = #{idUsuario}, "
            + "fecha_eliminacion = NOW(), ip_eliminacion = #{ip} WHERE id_contrato = #{idContrato} AND estado_registro = 'ACTIVO'")
    int eliminarContrato(@Param("idContrato") Integer idContrato, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Select("SELECT COUNT(*) FROM liquidacion WHERE id_contrato = #{idContrato} AND estado_registro = 'ACTIVO'")
    int contarLiquidacionesDeContrato(@Param("idContrato") Integer idContrato);

    // Unidad a la que pertenecia el contrato ANTES de aplicar un update, para
    // poder liberar esa unidad si el contrato se mueve a otra o deja de estar
    // VIGENTE (ver ImplContrato.insertUpdateContrato).
    @Select("SELECT id_unidad FROM contrato WHERE id_contrato = #{idContrato} AND estado_registro = 'ACTIVO'")
    Integer obtenerIdUnidadDeContrato(@Param("idContrato") Integer idContrato);

    // ------------------------------------------------------------------
    // CU-03b -- Renovar Contrato
    // ------------------------------------------------------------------

    @Results(id = "mapResultContratoParaRenovar", value = {
        @Result(column = "id_contrato", property = "idContrato"),
        @Result(column = "id_unidad", property = "idUnidad"),
        @Result(column = "numero_unidad", property = "numeroUnidad"),
        @Result(column = "id_inquilino", property = "idInquilino"),
        @Result(column = "nombre_inquilino", property = "nombreInquilino"),
        @Result(column = "fecha_inicio", property = "fechaInicio"),
        @Result(column = "fecha_fin", property = "fechaFin"),
        @Result(column = "monto_alquiler", property = "montoAlquiler"),
        @Result(column = "ruta_contrato_digital", property = "rutaContratoDigital"),
        @Result(column = "estado", property = "estado")
    })
    @Select("""
        SELECT c.id_contrato, c.id_unidad, u.numero_unidad, c.id_inquilino, i.nombre_completo AS nombre_inquilino,
               c.fecha_inicio, c.fecha_fin, c.monto_alquiler, c.ruta_contrato_digital, c.estado
        FROM contrato c
        JOIN unidad u ON u.id_unidad = c.id_unidad
        JOIN inquilino i ON i.id_inquilino = c.id_inquilino
        WHERE c.id_contrato = #{idContrato} AND c.estado_registro = 'ACTIVO'
        """)
    ContratoDTO obtenerContratoPorId(@Param("idContrato") Integer idContrato);

    // El contrato que se renueva no se elimina ni se sobreescribe: queda como
    // registro historico en estado RENOVADO, y CU-03b crea un contrato nuevo
    // que lo continua (ver ImplContrato.renovarContrato).
    @Update("UPDATE contrato SET estado = 'RENOVADO', usuario_actualizacion = #{idUsuario}, "
            + "fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_contrato = #{idContrato} AND estado_registro = 'ACTIVO'")
    int marcarContratoRenovado(@Param("idContrato") Integer idContrato, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    // ------------------------------------------------------------------
    // RN-03 / CU-03a -- Alertar Vencimiento de Contrato
    // ------------------------------------------------------------------

    // Un contrato VIGENTE cuya fecha_fin ya paso se marca VENCIDO. No es solo
    // cosmetico: mientras siga en VIGENTE, la restriccion unica
    // unidad_vigente_activa impide crear un contrato nuevo para esa unidad.
    // Se corre ANTES de marcarContratosVencidos, mientras el contrato
    // todavia figura VIGENTE: identifica exactamente las unidades que estan
    // OCUPADO por un contrato que esta a punto de vencer, y las libera. Si
    // se corriera despues (con el contrato ya en VENCIDO) se perderia el
    // criterio para saber cuales unidades corresponden.
    @Update("""
        UPDATE unidad u
        JOIN contrato c ON c.id_unidad = u.id_unidad
        SET u.estado = 'DISPONIBLE', u.usuario_actualizacion = #{idUsuario},
            u.fecha_actualizacion = NOW(), u.ip_actualizacion = #{ip}
        WHERE c.estado = 'VIGENTE' AND c.estado_registro = 'ACTIVO' AND c.fecha_fin < CURDATE()
          AND u.estado = 'OCUPADO' AND u.estado_registro = 'ACTIVO'
        """)
    int liberarUnidadesDeContratosPorVencer(@Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE contrato SET estado = 'VENCIDO', usuario_actualizacion = #{idUsuario}, "
            + "fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE estado = 'VIGENTE' AND estado_registro = 'ACTIVO' AND fecha_fin < CURDATE()")
    int marcarContratosVencidos(@Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Results(id = "mapListResultContratoPorVencer", value = {
        @Result(column = "id_contrato", property = "idContrato"),
        @Result(column = "numero_unidad", property = "numeroUnidad"),
        @Result(column = "nombre_inquilino", property = "nombreInquilino"),
        @Result(column = "fecha_fin", property = "fechaFin"),
        @Result(column = "dias_para_vencer", property = "diasParaVencer")
    })
    @Select("""
        SELECT c.id_contrato, u.numero_unidad, i.nombre_completo AS nombre_inquilino, c.fecha_fin,
               DATEDIFF(c.fecha_fin, CURDATE()) AS dias_para_vencer
        FROM contrato c
        JOIN unidad u ON u.id_unidad = c.id_unidad
        JOIN inquilino i ON i.id_inquilino = c.id_inquilino
        WHERE c.estado = 'VIGENTE' AND c.estado_registro = 'ACTIVO'
          AND c.fecha_fin BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL #{dias} DAY)
        ORDER BY c.fecha_fin
        """)
    List<ContratoPorVencerDTO> listarContratosPorVencer(@Param("dias") int dias);
}
