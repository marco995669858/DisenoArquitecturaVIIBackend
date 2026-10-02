package com.dataverse.rentals.security;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * Freno de fuerza bruta contra /auth/login. Se consulta con dos claves a la
 * vez (la IP real y el nombre de usuario del intento) para que ninguno de
 * los dos caminos de evasion funcione solo: rotar de IP no sirve porque la
 * cuenta igual queda bloqueada por nombre de usuario, y probar contra
 * cuentas distintas desde la misma maquina igual la frena la IP.
 *
 * Solo cuentan los intentos con contraseña incorrecta (BadCredentialsException
 * en ImplUsuario.login) -- un usuario legitimo que aun no completa el paso de
 * MFA no debe quedar bloqueado por sus propios intentos correctos.
 *
 * En memoria (no en base de datos ni Redis): es intencional, un reinicio del
 * backend limpia el historial, lo cual es aceptable para este freno -- no es
 * una lista negra permanente, es solo para hacer lento un ataque en curso.
 */
@Component
public class LoginAttemptTracker {

    private static final int MAX_INTENTOS = 5;
    private static final Duration VENTANA = Duration.ofMinutes(1);
    private static final Duration BLOQUEO = Duration.ofMinutes(5);

    private static class Historial {
        final Deque<Instant> fallos = new ArrayDeque<>();
        Instant bloqueadoHasta;
    }

    private final ConcurrentHashMap<String, Historial> porClave = new ConcurrentHashMap<>();

    /** Cuanto falta para poder reintentar con esta clave (IP o usuario); Duration.ZERO si no esta bloqueada. */
    public Duration tiempoRestanteBloqueo(String clave) {
        Historial historial = porClave.get(clave);
        if (historial == null) {
            return Duration.ZERO;
        }
        synchronized (historial) {
            Instant ahora = Instant.now();
            if (historial.bloqueadoHasta != null) {
                if (ahora.isBefore(historial.bloqueadoHasta)) {
                    return Duration.between(ahora, historial.bloqueadoHasta);
                }
                // el bloqueo ya expiro: se limpia para que el siguiente fallo empiece de cero
                historial.bloqueadoHasta = null;
                historial.fallos.clear();
            }
            return Duration.ZERO;
        }
    }

    /** Registra una contraseña incorrecta; si llega al limite dentro de la ventana, bloquea la clave. */
    public void registrarFallo(String clave) {
        Historial historial = porClave.computeIfAbsent(clave, k -> new Historial());
        synchronized (historial) {
            Instant ahora = Instant.now();
            historial.fallos.addLast(ahora);
            while (!historial.fallos.isEmpty() && Duration.between(historial.fallos.peekFirst(), ahora).compareTo(VENTANA) > 0) {
                historial.fallos.pollFirst();
            }
            if (historial.fallos.size() >= MAX_INTENTOS) {
                historial.bloqueadoHasta = ahora.plus(BLOQUEO);
            }
        }
    }

    /** Un login exitoso borra el historial de esa clave: no tiene sentido seguir contando fallos viejos. */
    public void registrarExito(String clave) {
        porClave.remove(clave);
    }
}
