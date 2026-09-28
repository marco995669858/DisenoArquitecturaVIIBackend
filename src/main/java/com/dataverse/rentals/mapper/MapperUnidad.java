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

import com.dataverse.rentals.model.unidad.request.UnidadRequest;
import com.dataverse.rentals.model.unidad.response.UnidadDTO;

@Mapper
public interface MapperUnidad {

    @Results(id = "mapListResultUnidad", value = {
        @Result(column = "id_unidad", property = "idUnidad"),
        @Result(column = "piso", property = "piso"),
        @Result(column = "numero_unidad", property = "numeroUnidad"),
        @Result(column = "area_m2", property = "areaM2"),
        @Result(column = "caracteristicas", property = "caracteristicas"),
        @Result(column = "estado", property = "estado")
    })
    @Select("SELECT id_unidad, piso, numero_unidad, area_m2, caracteristicas, estado FROM unidad "
            + "WHERE estado_registro = 'ACTIVO' ORDER BY piso, numero_unidad")
    List<UnidadDTO> listarUnidades();

    @Options(useGeneratedKeys = true, keyProperty = "args.idUnidad")
    @Insert("INSERT INTO unidad (piso, numero_unidad, area_m2, caracteristicas, estado, usuario_creacion, ip_creacion) "
            + "VALUES (#{args.piso}, #{args.numeroUnidad}, #{args.areaM2}, #{args.caracteristicas}, #{args.estado}, #{idUsuario}, #{ip})")
    int insertarUnidad(@Param("args") UnidadRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE unidad SET piso = #{args.piso}, numero_unidad = #{args.numeroUnidad}, area_m2 = #{args.areaM2}, "
            + "caracteristicas = #{args.caracteristicas}, estado = #{args.estado}, "
            + "usuario_actualizacion = #{idUsuario}, fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_unidad = #{args.idUnidad} AND estado_registro = 'ACTIVO'")
    int actualizarUnidad(@Param("args") UnidadRequest args, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Update("UPDATE unidad SET estado_registro = 'ELIMINADO', usuario_eliminacion = #{idUsuario}, "
            + "fecha_eliminacion = NOW(), ip_eliminacion = #{ip} WHERE id_unidad = #{idUnidad} AND estado_registro = 'ACTIVO'")
    int eliminarUnidad(@Param("idUnidad") Integer idUnidad, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    @Select("SELECT COUNT(*) FROM contrato WHERE id_unidad = #{idUnidad} AND estado_registro = 'ACTIVO'")
    int contarContratosDeUnidad(@Param("idUnidad") Integer idUnidad);

    @Select("SELECT COUNT(*) FROM medidor WHERE id_unidad = #{idUnidad} AND estado_registro = 'ACTIVO'")
    int contarMedidoresDeUnidad(@Param("idUnidad") Integer idUnidad);

    @Select("SELECT COUNT(*) FROM prorrateo WHERE id_unidad = #{idUnidad} AND estado_registro = 'ACTIVO'")
    int contarProrrateosDeUnidad(@Param("idUnidad") Integer idUnidad);

    @Select("SELECT COUNT(*) FROM mantenimiento WHERE id_unidad = #{idUnidad} AND estado_registro = 'ACTIVO'")
    int contarMantenimientosDeUnidad(@Param("idUnidad") Integer idUnidad);

    // ------------------------------------------------------------------
    // Sincronizacion de unidad.estado con el ciclo de vida del contrato --
    // ver ImplContrato. "estado" es manual solo entre DISPONIBLE y
    // MANTENIMIENTO; OCUPADO lo pone y lo quita el sistema, nunca el
    // usuario, para que no pueda quedar desalineado de si existe o no un
    // contrato VIGENTE.
    // ------------------------------------------------------------------

    @Update("UPDATE unidad SET estado = 'OCUPADO', usuario_actualizacion = #{idUsuario}, "
            + "fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_unidad = #{idUnidad} AND estado_registro = 'ACTIVO'")
    int marcarUnidadOcupada(@Param("idUnidad") Integer idUnidad, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);

    // Solo libera si estaba OCUPADO: si esta en MANTENIMIENTO no se pisa ese
    // estado solo porque el contrato que la ocupaba termino.
    @Update("UPDATE unidad SET estado = 'DISPONIBLE', usuario_actualizacion = #{idUsuario}, "
            + "fecha_actualizacion = NOW(), ip_actualizacion = #{ip} "
            + "WHERE id_unidad = #{idUnidad} AND estado = 'OCUPADO' AND estado_registro = 'ACTIVO'")
    int marcarUnidadDisponible(@Param("idUnidad") Integer idUnidad, @Param("idUsuario") Integer idUsuario, @Param("ip") String ip);
}
