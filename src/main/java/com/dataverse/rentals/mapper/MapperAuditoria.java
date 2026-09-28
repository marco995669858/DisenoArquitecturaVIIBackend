package com.dataverse.rentals.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.dataverse.rentals.model.auditoria.response.EventoAuditoriaDTO;

/**
 * RNF-06 / trazabilidad: no existe una tabla central de auditoria -- cada
 * tabla de negocio ya trae su propio trio usuario/fecha/ip de creacion,
 * actualizacion y eliminacion (ver comentario al inicio de db/schema.sql).
 * Este mapper convierte esas columnas, esparcidas en 8 tablas, en una sola
 * linea de tiempo: cada fila de negocio puede aportar hasta 3 eventos
 * (creacion siempre; actualizacion/eliminacion solo si esa fecha no es
 * NULL), unidos con UNION ALL y ordenados por fecha.
 */
@Mapper
public interface MapperAuditoria {

    @Results(id = "mapEventoAuditoria", value = {
        @Result(column = "fecha", property = "fecha"),
        @Result(column = "accion", property = "accion"),
        @Result(column = "entidad", property = "entidad"),
        @Result(column = "descripcion", property = "descripcion"),
        @Result(column = "nombre_usuario", property = "nombreUsuario"),
        @Result(column = "ip", property = "ip")
    })
    @Select("""
        (SELECT t.fecha_creacion AS fecha, 'CREACION' AS accion, 'Unidad' AS entidad,
                CONCAT('Unidad ', t.numero_unidad, ' (piso ', t.piso, ')') AS descripcion,
                COALESCE(u.nombre_completo, 'Sistema') AS nombre_usuario, t.ip_creacion AS ip
         FROM unidad t LEFT JOIN usuario u ON u.id_usuario = t.usuario_creacion)
        UNION ALL
        (SELECT t.fecha_actualizacion, 'ACTUALIZACION', 'Unidad',
                CONCAT('Unidad ', t.numero_unidad, ' (piso ', t.piso, ')'),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_actualizacion
         FROM unidad t LEFT JOIN usuario u ON u.id_usuario = t.usuario_actualizacion
         WHERE t.fecha_actualizacion IS NOT NULL)
        UNION ALL
        (SELECT t.fecha_eliminacion, 'ELIMINACION', 'Unidad',
                CONCAT('Unidad ', t.numero_unidad, ' (piso ', t.piso, ')'),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_eliminacion
         FROM unidad t LEFT JOIN usuario u ON u.id_usuario = t.usuario_eliminacion
         WHERE t.fecha_eliminacion IS NOT NULL)

        UNION ALL
        (SELECT t.fecha_creacion, 'CREACION', 'Inquilino',
                CONCAT(t.nombre_completo, ' (', t.tipo_documento, ' ', t.numero_documento, ')'),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_creacion
         FROM inquilino t LEFT JOIN usuario u ON u.id_usuario = t.usuario_creacion)
        UNION ALL
        (SELECT t.fecha_actualizacion, 'ACTUALIZACION', 'Inquilino',
                CONCAT(t.nombre_completo, ' (', t.tipo_documento, ' ', t.numero_documento, ')'),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_actualizacion
         FROM inquilino t LEFT JOIN usuario u ON u.id_usuario = t.usuario_actualizacion
         WHERE t.fecha_actualizacion IS NOT NULL)
        UNION ALL
        (SELECT t.fecha_eliminacion, 'ELIMINACION', 'Inquilino',
                CONCAT(t.nombre_completo, ' (', t.tipo_documento, ' ', t.numero_documento, ')'),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_eliminacion
         FROM inquilino t LEFT JOIN usuario u ON u.id_usuario = t.usuario_eliminacion
         WHERE t.fecha_eliminacion IS NOT NULL)

        UNION ALL
        (SELECT t.fecha_creacion, 'CREACION', 'Contrato',
                CONCAT('Contrato unidad ', un.numero_unidad, ' - ', i.nombre_completo),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_creacion
         FROM contrato t
         JOIN unidad un ON un.id_unidad = t.id_unidad
         JOIN inquilino i ON i.id_inquilino = t.id_inquilino
         LEFT JOIN usuario u ON u.id_usuario = t.usuario_creacion)
        UNION ALL
        (SELECT t.fecha_actualizacion, 'ACTUALIZACION', 'Contrato',
                CONCAT('Contrato unidad ', un.numero_unidad, ' - ', i.nombre_completo, ' (', t.estado, ')'),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_actualizacion
         FROM contrato t
         JOIN unidad un ON un.id_unidad = t.id_unidad
         JOIN inquilino i ON i.id_inquilino = t.id_inquilino
         LEFT JOIN usuario u ON u.id_usuario = t.usuario_actualizacion
         WHERE t.fecha_actualizacion IS NOT NULL)
        UNION ALL
        (SELECT t.fecha_eliminacion, 'ELIMINACION', 'Contrato',
                CONCAT('Contrato unidad ', un.numero_unidad, ' - ', i.nombre_completo),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_eliminacion
         FROM contrato t
         JOIN unidad un ON un.id_unidad = t.id_unidad
         JOIN inquilino i ON i.id_inquilino = t.id_inquilino
         LEFT JOIN usuario u ON u.id_usuario = t.usuario_eliminacion
         WHERE t.fecha_eliminacion IS NOT NULL)

        UNION ALL
        (SELECT t.fecha_creacion, 'CREACION', 'Lectura de medidor',
                CONCAT('Lectura ', m.tipo_servicio, ' periodo ', t.periodo,
                       CASE WHEN m.id_unidad IS NOT NULL THEN CONCAT(' unidad ', un.numero_unidad) ELSE ' (medidor general)' END),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_creacion
         FROM lectura_medidor t
         JOIN medidor m ON m.id_medidor = t.id_medidor
         LEFT JOIN unidad un ON un.id_unidad = m.id_unidad
         LEFT JOIN usuario u ON u.id_usuario = t.usuario_creacion)
        UNION ALL
        (SELECT t.fecha_actualizacion, 'ACTUALIZACION', 'Lectura de medidor',
                CONCAT('Lectura ', m.tipo_servicio, ' periodo ', t.periodo,
                       CASE WHEN m.id_unidad IS NOT NULL THEN CONCAT(' unidad ', un.numero_unidad) ELSE ' (medidor general)' END),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_actualizacion
         FROM lectura_medidor t
         JOIN medidor m ON m.id_medidor = t.id_medidor
         LEFT JOIN unidad un ON un.id_unidad = m.id_unidad
         LEFT JOIN usuario u ON u.id_usuario = t.usuario_actualizacion
         WHERE t.fecha_actualizacion IS NOT NULL)
        UNION ALL
        (SELECT t.fecha_eliminacion, 'ELIMINACION', 'Lectura de medidor',
                CONCAT('Lectura ', m.tipo_servicio, ' periodo ', t.periodo,
                       CASE WHEN m.id_unidad IS NOT NULL THEN CONCAT(' unidad ', un.numero_unidad) ELSE ' (medidor general)' END),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_eliminacion
         FROM lectura_medidor t
         JOIN medidor m ON m.id_medidor = t.id_medidor
         LEFT JOIN unidad un ON un.id_unidad = m.id_unidad
         LEFT JOIN usuario u ON u.id_usuario = t.usuario_eliminacion
         WHERE t.fecha_eliminacion IS NOT NULL)

        UNION ALL
        (SELECT t.fecha_creacion, 'CREACION', 'Liquidacion',
                CONCAT('Liquidacion ', t.periodo, ' unidad ', un.numero_unidad, ' (S/ ', t.monto_total, ')'),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_creacion
         FROM liquidacion t
         JOIN contrato c ON c.id_contrato = t.id_contrato
         JOIN unidad un ON un.id_unidad = c.id_unidad
         LEFT JOIN usuario u ON u.id_usuario = t.usuario_creacion)
        UNION ALL
        (SELECT t.fecha_actualizacion, 'ACTUALIZACION', 'Liquidacion',
                CONCAT('Liquidacion ', t.periodo, ' unidad ', un.numero_unidad, ' (', t.estado, ')'),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_actualizacion
         FROM liquidacion t
         JOIN contrato c ON c.id_contrato = t.id_contrato
         JOIN unidad un ON un.id_unidad = c.id_unidad
         LEFT JOIN usuario u ON u.id_usuario = t.usuario_actualizacion
         WHERE t.fecha_actualizacion IS NOT NULL)
        UNION ALL
        (SELECT t.fecha_eliminacion, 'ELIMINACION', 'Liquidacion',
                CONCAT('Liquidacion ', t.periodo, ' unidad ', un.numero_unidad),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_eliminacion
         FROM liquidacion t
         JOIN contrato c ON c.id_contrato = t.id_contrato
         JOIN unidad un ON un.id_unidad = c.id_unidad
         LEFT JOIN usuario u ON u.id_usuario = t.usuario_eliminacion
         WHERE t.fecha_eliminacion IS NOT NULL)

        UNION ALL
        (SELECT t.fecha_creacion, 'CREACION', 'Pago',
                CONCAT('Pago S/ ', t.monto_pagado, ' - liquidacion ', li.periodo, ' unidad ', un.numero_unidad),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_creacion
         FROM pago t
         JOIN liquidacion li ON li.id_liquidacion = t.id_liquidacion
         JOIN contrato c ON c.id_contrato = li.id_contrato
         JOIN unidad un ON un.id_unidad = c.id_unidad
         LEFT JOIN usuario u ON u.id_usuario = t.usuario_creacion)
        UNION ALL
        (SELECT t.fecha_actualizacion, 'ACTUALIZACION', 'Pago',
                CONCAT('Pago S/ ', t.monto_pagado, ' - liquidacion ', li.periodo, ' unidad ', un.numero_unidad),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_actualizacion
         FROM pago t
         JOIN liquidacion li ON li.id_liquidacion = t.id_liquidacion
         JOIN contrato c ON c.id_contrato = li.id_contrato
         JOIN unidad un ON un.id_unidad = c.id_unidad
         LEFT JOIN usuario u ON u.id_usuario = t.usuario_actualizacion
         WHERE t.fecha_actualizacion IS NOT NULL)
        UNION ALL
        (SELECT t.fecha_eliminacion, 'ELIMINACION', 'Pago',
                CONCAT('Pago S/ ', t.monto_pagado, ' - liquidacion ', li.periodo, ' unidad ', un.numero_unidad),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_eliminacion
         FROM pago t
         JOIN liquidacion li ON li.id_liquidacion = t.id_liquidacion
         JOIN contrato c ON c.id_contrato = li.id_contrato
         JOIN unidad un ON un.id_unidad = c.id_unidad
         LEFT JOIN usuario u ON u.id_usuario = t.usuario_eliminacion
         WHERE t.fecha_eliminacion IS NOT NULL)

        UNION ALL
        (SELECT t.fecha_creacion, 'CREACION', 'Mantenimiento',
                CONCAT('Mantenimiento: ', t.descripcion,
                       CASE WHEN t.id_unidad IS NOT NULL THEN CONCAT(' (unidad ', un.numero_unidad, ')') ELSE ' (areas comunes)' END),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_creacion
         FROM mantenimiento t
         LEFT JOIN unidad un ON un.id_unidad = t.id_unidad
         LEFT JOIN usuario u ON u.id_usuario = t.usuario_creacion)
        UNION ALL
        (SELECT t.fecha_actualizacion, 'ACTUALIZACION', 'Mantenimiento',
                CONCAT('Mantenimiento: ', t.descripcion,
                       CASE WHEN t.id_unidad IS NOT NULL THEN CONCAT(' (unidad ', un.numero_unidad, ')') ELSE ' (areas comunes)' END),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_actualizacion
         FROM mantenimiento t
         LEFT JOIN unidad un ON un.id_unidad = t.id_unidad
         LEFT JOIN usuario u ON u.id_usuario = t.usuario_actualizacion
         WHERE t.fecha_actualizacion IS NOT NULL)
        UNION ALL
        (SELECT t.fecha_eliminacion, 'ELIMINACION', 'Mantenimiento',
                CONCAT('Mantenimiento: ', t.descripcion,
                       CASE WHEN t.id_unidad IS NOT NULL THEN CONCAT(' (unidad ', un.numero_unidad, ')') ELSE ' (areas comunes)' END),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_eliminacion
         FROM mantenimiento t
         LEFT JOIN unidad un ON un.id_unidad = t.id_unidad
         LEFT JOIN usuario u ON u.id_usuario = t.usuario_eliminacion
         WHERE t.fecha_eliminacion IS NOT NULL)

        UNION ALL
        (SELECT t.fecha_creacion, 'CREACION', 'Usuario',
                CONCAT('Usuario ', t.nombre_usuario, ' (', t.nombre_completo, ')'),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_creacion
         FROM usuario t LEFT JOIN usuario u ON u.id_usuario = t.usuario_creacion)
        UNION ALL
        (SELECT t.fecha_actualizacion, 'ACTUALIZACION', 'Usuario',
                CONCAT('Usuario ', t.nombre_usuario, ' (', t.nombre_completo, ')'),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_actualizacion
         FROM usuario t LEFT JOIN usuario u ON u.id_usuario = t.usuario_actualizacion
         WHERE t.fecha_actualizacion IS NOT NULL)
        UNION ALL
        (SELECT t.fecha_eliminacion, 'ELIMINACION', 'Usuario',
                CONCAT('Usuario ', t.nombre_usuario, ' (', t.nombre_completo, ')'),
                COALESCE(u.nombre_completo, 'Sistema'), t.ip_eliminacion
         FROM usuario t LEFT JOIN usuario u ON u.id_usuario = t.usuario_eliminacion
         WHERE t.fecha_eliminacion IS NOT NULL)

        ORDER BY fecha DESC
        LIMIT #{limite}
        """)
    List<EventoAuditoriaDTO> listarEventos(@Param("limite") int limite);
}
