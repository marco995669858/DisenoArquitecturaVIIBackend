package com.dataverse.rentals.security;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

/**
 * TOTP (RFC 6238) implementado a mano, sin agregar ninguna libreria externa
 * al proyecto -- el build de Maven corre en modo offline (-o) en este
 * entorno, y una dependencia nueva no descargada rompe el compilado. El
 * algoritmo es el mismo que usan Google Authenticator, Microsoft
 * Authenticator, Authy, etc.: HMAC-SHA1, 6 digitos, paso de 30 segundos
 * (RFC 4226 + RFC 6238).
 */
@Component
public class TotpUtil {

    private static final String BASE32_ALFABETO = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final int SECRETO_BYTES = 20; // 160 bits: el tamano estandar para HMAC-SHA1
    private static final int DIGITOS = 6;
    private static final int PASO_SEGUNDOS = 30;
    private static final int VENTANA_TOLERANCIA = 1; // +-1 paso (30s) por desfase de reloj entre el celular y el servidor
    private static final String ISSUER = "DataverseRentals";

    public String generarSecreto() {
        byte[] bytes = new byte[SECRETO_BYTES];
        new SecureRandom().nextBytes(bytes);
        return base32Encode(bytes);
    }

    /** URI que cualquier app de autenticacion (Google/Microsoft Authenticator, Authy) entiende al escanear el QR. */
    public String generarUriOtpAuth(String secretoBase32, String nombreUsuario) {
        return String.format(Locale.ROOT,
                "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=%d&period=%d",
                ISSUER, nombreUsuario, secretoBase32, ISSUER, DIGITOS, PASO_SEGUNDOS);
    }

    public boolean validarCodigo(String secretoBase32, String codigoIngresado) {
        return validarCodigo(secretoBase32, codigoIngresado, System.currentTimeMillis());
    }

    /**
     * Version con el instante fijo, visible para las pruebas: permite
     * validar contra los vectores de prueba oficiales de la RFC 6238 (que
     * fijan el "tiempo actual" a un valor conocido) sin depender del reloj
     * real de la maquina.
     */
    boolean validarCodigo(String secretoBase32, String codigoIngresado, long epochMillis) {
        if (secretoBase32 == null || codigoIngresado == null || !codigoIngresado.matches("\\d{6}")) {
            return false;
        }
        long pasoActual = epochMillis / 1000L / PASO_SEGUNDOS;
        byte[] secreto = base32Decode(secretoBase32);
        for (int i = -VENTANA_TOLERANCIA; i <= VENTANA_TOLERANCIA; i++) {
            if (generarCodigo(secreto, pasoActual + i).equals(codigoIngresado)) {
                return true;
            }
        }
        return false;
    }

    // Sin "private": el test unitario (mismo paquete) genera codigos de
    // referencia directamente para verificar generarCodigo/base32Encode por
    // separado, ademas de validarCodigo(...) de punta a punta.
    String generarCodigo(byte[] secreto, long paso) {
        try {
            byte[] data = ByteBuffer.allocate(8).putLong(paso).array();
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(secreto, "HmacSHA1"));
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0x0F;
            int binario = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);

            int codigo = binario % (int) Math.pow(10, DIGITOS);
            return String.format(Locale.ROOT, "%0" + DIGITOS + "d", codigo);
        } catch (Exception e) {
            throw new RuntimeException("Error generando codigo TOTP", e);
        }
    }

    String base32Encode(byte[] datos) {
        StringBuilder resultado = new StringBuilder();
        int buffer = 0;
        int bitsEnBuffer = 0;
        for (byte b : datos) {
            buffer = (buffer << 8) | (b & 0xFF);
            bitsEnBuffer += 8;
            while (bitsEnBuffer >= 5) {
                int indice = (buffer >> (bitsEnBuffer - 5)) & 0x1F;
                resultado.append(BASE32_ALFABETO.charAt(indice));
                bitsEnBuffer -= 5;
            }
        }
        if (bitsEnBuffer > 0) {
            int indice = (buffer << (5 - bitsEnBuffer)) & 0x1F;
            resultado.append(BASE32_ALFABETO.charAt(indice));
        }
        return resultado.toString();
    }

    private byte[] base32Decode(String base32) {
        String limpio = base32.trim().toUpperCase(Locale.ROOT).replace("=", "");
        int bufferSize = limpio.length() * 5 / 8;
        byte[] resultado = new byte[bufferSize];
        int buffer = 0;
        int bitsEnBuffer = 0;
        int indiceResultado = 0;
        for (char c : limpio.toCharArray()) {
            int valor = BASE32_ALFABETO.indexOf(c);
            if (valor < 0) continue;
            buffer = (buffer << 5) | valor;
            bitsEnBuffer += 5;
            if (bitsEnBuffer >= 8) {
                resultado[indiceResultado++] = (byte) ((buffer >> (bitsEnBuffer - 8)) & 0xFF);
                bitsEnBuffer -= 8;
            }
        }
        return resultado;
    }
}
