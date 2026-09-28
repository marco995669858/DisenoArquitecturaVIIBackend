package com.dataverse.rentals.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * TOTP se implemento a mano (sin libreria externa, ver TotpUtil), asi que la
 * unica forma de estar seguros de que es compatible con Google/Microsoft
 * Authenticator de verdad -- no solo consigo mismo -- es probarlo contra los
 * vectores de prueba oficiales de la RFC 6238 (Apendice B), que usan el
 * secreto ASCII "12345678901234567890" e instantes Unix fijos con su codigo
 * esperado. La RFC publica codigos de 8 digitos; los ultimos 6 coinciden con
 * los que produce esta clase porque ambos salen del mismo binario truncado,
 * solo cambia el modulo (10^8 vs 10^6).
 */
class TotpUtilTest {

    private static final String SECRETO_RFC_ASCII = "12345678901234567890";

    private TotpUtil totpUtil;
    private String secretoBase32;

    @BeforeEach
    void setUp() {
        totpUtil = new TotpUtil();
        secretoBase32 = totpUtil.base32Encode(SECRETO_RFC_ASCII.getBytes(StandardCharsets.US_ASCII));
    }

    @ParameterizedTest(name = "tiempo Unix {0}s -> codigo {1}")
    @CsvSource({
        "59, 287082",
        "1111111109, 081804",
        "1111111111, 050471",
        "1234567890, 005924",
        "2000000000, 279037"
    })
    void validaLosCodigosOficialesDeLaRfc6238(long tiempoUnixSegundos, String codigoEsperado) {
        boolean valido = totpUtil.validarCodigo(secretoBase32, codigoEsperado, tiempoUnixSegundos * 1000L);
        assertThat(valido).isTrue();
    }

    @Test
    void rechazaUnCodigoIncorrectoEnElMismoInstante() {
        boolean valido = totpUtil.validarCodigo(secretoBase32, "000000", 59_000L);
        assertThat(valido).isFalse();
    }

    @Test
    void rechazaUnCodigoQueYaSalioDeLaVentanaDeTolerancia() {
        // El codigo de Time=59 (paso 1) solo es valido en los pasos 0,1,2
        // (+-1 de tolerancia); una hora despues (paso ~120) ya no debe pasar.
        boolean valido = totpUtil.validarCodigo(secretoBase32, "287082", (59 + 3600) * 1000L);
        assertThat(valido).isFalse();
    }

    @Test
    void aceptaElCodigoDelPasoAnteriorYDelSiguientePorLaToleranciaDeUnPaso() {
        // Time=59 -> paso 1. Un instante en el paso 2 (Time=89) todavia debe
        // aceptar el codigo del paso 1, gracias a la ventana de +-1 paso.
        boolean valido = totpUtil.validarCodigo(secretoBase32, "287082", 89_000L);
        assertThat(valido).isTrue();
    }

    @Test
    void rechazaFormatosInvalidosSinExplotar() {
        assertThat(totpUtil.validarCodigo(null, "123456")).isFalse();
        assertThat(totpUtil.validarCodigo(secretoBase32, null)).isFalse();
        assertThat(totpUtil.validarCodigo(secretoBase32, "12ab56")).isFalse();
        assertThat(totpUtil.validarCodigo(secretoBase32, "12345")).isFalse(); // 5 digitos, no 6
    }

    @Test
    void generarSecretoProduceBase32ValidoYDeLargoConsistente() {
        String secreto = totpUtil.generarSecreto();

        assertThat(secreto).matches("[A-Z2-7]+"); // alfabeto Base32 estandar (RFC 4648), sin relleno '='
        assertThat(secreto).hasSize(32); // 20 bytes * 8 bits / 5 bits por caracter, redondeado hacia arriba
    }

    @Test
    void generarSecretoNuncaRepiteElMismoValor() {
        // Es aleatorio (SecureRandom): dos llamadas no deberian coincidir.
        assertThat(totpUtil.generarSecreto()).isNotEqualTo(totpUtil.generarSecreto());
    }

    @Test
    void generarUriOtpAuthTraeTodoLoQueUnaAppDeAutenticacionNecesitaParaElQr() {
        String uri = totpUtil.generarUriOtpAuth("ABCDEFGH", "admin");

        assertThat(uri).startsWith("otpauth://totp/DataverseRentals:admin");
        assertThat(uri).contains("secret=ABCDEFGH");
        assertThat(uri).contains("issuer=DataverseRentals");
        assertThat(uri).contains("algorithm=SHA1");
        assertThat(uri).contains("digits=6");
        assertThat(uri).contains("period=30");
    }
}
