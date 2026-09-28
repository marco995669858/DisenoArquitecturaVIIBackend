package com.dataverse.rentals.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.dataverse.rentals.model.auditoria.response.EventoAuditoriaDTO;

/**
 * El UNION ALL de MapperAuditoria (24 SELECT distintos, 8 tablas x 3 eventos)
 * no se puede verificar con un mock: un error de columna ambigua, un JOIN mal
 * escrito o un tipo incompatible entre ramas del UNION solo lo detecta MySQL
 * al ejecutar la consulta real. Por eso esta es una prueba de integracion
 * (@SpringBootTest, contra la base de datos configurada en .env) y no una
 * prueba unitaria con Mockito como el resto del paquete service.impl.
 */
@SpringBootTest
class MapperAuditoriaIntegrationTest {

    @Autowired
    private MapperAuditoria mapper;

    @Test
    void elUnionDeLasOchoTablasSeEjecutaSinErroresYQuedaOrdenadoPorFechaDescendente() {
        List<EventoAuditoriaDTO> eventos = mapper.listarEventos(50);

        assertThat(eventos).isNotEmpty();
        for (EventoAuditoriaDTO evento : eventos) {
            assertThat(evento.getFecha()).isNotNull();
            assertThat(evento.getAccion()).isIn("CREACION", "ACTUALIZACION", "ELIMINACION");
            assertThat(evento.getEntidad()).isNotBlank();
            assertThat(evento.getDescripcion()).isNotBlank();
            assertThat(evento.getNombreUsuario()).isNotBlank();
        }

        for (int i = 1; i < eventos.size(); i++) {
            assertThat(eventos.get(i - 1).getFecha()).isAfterOrEqualTo(eventos.get(i).getFecha());
        }
    }

    @Test
    void respetaElLimiteSolicitado() {
        List<EventoAuditoriaDTO> eventos = mapper.listarEventos(3);

        assertThat(eventos).hasSizeLessThanOrEqualTo(3);
    }
}
