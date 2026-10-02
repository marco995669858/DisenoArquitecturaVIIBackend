package com.dataverse.rentals.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;

class LoginAttemptTrackerTest {

    @Test
    void noBloqueaUnaClaveQueNuncaFallo() {
        LoginAttemptTracker tracker = new LoginAttemptTracker();

        assertThat(tracker.tiempoRestanteBloqueo("ip:1.2.3.4")).isZero();
    }

    @Test
    void noBloqueaAntesDeLlegarAlLimiteDeIntentos() {
        LoginAttemptTracker tracker = new LoginAttemptTracker();

        for (int i = 0; i < 4; i++) {
            tracker.registrarFallo("ip:1.2.3.4");
        }

        assertThat(tracker.tiempoRestanteBloqueo("ip:1.2.3.4")).isZero();
    }

    @Test
    void bloqueaAlQuintoIntentoFallidoDentroDeLaVentana() {
        LoginAttemptTracker tracker = new LoginAttemptTracker();

        for (int i = 0; i < 5; i++) {
            tracker.registrarFallo("ip:1.2.3.4");
        }

        Duration restante = tracker.tiempoRestanteBloqueo("ip:1.2.3.4");

        assertThat(restante).isNotEqualTo(Duration.ZERO);
        assertThat(restante).isLessThanOrEqualTo(Duration.ofMinutes(5));
    }

    @Test
    void unaClaveDistintaNoSeVeAfectadaPorLosFallosDeOtra() {
        LoginAttemptTracker tracker = new LoginAttemptTracker();

        for (int i = 0; i < 5; i++) {
            tracker.registrarFallo("ip:1.2.3.4");
        }

        // Rotar de IP no deberia servir para seguir probando contra el mismo usuario:
        // esta prueba confirma la otra mitad -- que las claves son independientes,
        // que es justamente lo que permite usar IP y usuario como dos claves separadas.
        assertThat(tracker.tiempoRestanteBloqueo("user:admin")).isZero();
    }

    @Test
    void unExitoLimpiaElHistorialDeFallosDeEsaClave() {
        LoginAttemptTracker tracker = new LoginAttemptTracker();

        for (int i = 0; i < 4; i++) {
            tracker.registrarFallo("ip:1.2.3.4");
        }
        tracker.registrarExito("ip:1.2.3.4");

        // Si no se hubiera limpiado, este quinto fallo (el primero despues del
        // exito) completaria 5 y bloquearia; como se limpio, es solo el primero.
        tracker.registrarFallo("ip:1.2.3.4");

        assertThat(tracker.tiempoRestanteBloqueo("ip:1.2.3.4")).isZero();
    }
}
