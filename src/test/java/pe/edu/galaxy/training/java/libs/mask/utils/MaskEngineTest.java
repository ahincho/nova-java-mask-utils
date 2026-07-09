package pe.edu.galaxy.training.java.libs.mask.utils;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import pe.edu.galaxy.training.java.libs.mask.utils.config.MaskConfig;
import pe.edu.galaxy.training.java.libs.mask.utils.result.MaskResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link MaskEngine}.
 * Verifica API estática, builder, manejo de nulos/vacíos y todos los tipos.
 */
@DisplayName("MaskEngine — Motor central de enmascaramiento")
class MaskEngineTest {

    @Nested
    @DisplayName("API estática — Todos los tipos")
    class ApiEstatica {

        /** Verifica enmascaramiento de email vía API estática. */
        @Test
        void deberiaEnmascararEmail() {
            MaskResult result = MaskEngine.mask("john@gmail.com", MaskType.EMAIL, CountryCode.GENERIC);
            assertEquals("j***@gmail.com", result.maskedValue());
            assertTrue(result.wasMasked());
        }

        /** Verifica enmascaramiento de teléfono peruano. */
        @Test
        void deberiaEnmascararTelefonoPeru() {
            MaskResult result = MaskEngine.mask("+51 987 654 321", MaskType.PHONE, CountryCode.PE);
            assertEquals("+51 *** *** 321", result.maskedValue());
        }

        /** Verifica enmascaramiento de DNI peruano. */
        @Test
        void deberiaEnmascararDniPeru() {
            MaskResult result = MaskEngine.mask("12345678", MaskType.IDENTITY_DOCUMENT, CountryCode.PE);
            assertEquals("*****678", result.maskedValue());
        }

        /** Verifica enmascaramiento de tarjeta de crédito. */
        @Test
        void deberiaEnmascararTarjeta() {
            MaskResult result = MaskEngine.mask("4532012345678901", MaskType.CREDIT_CARD, CountryCode.GENERIC);
            assertEquals("453201******8901", result.maskedValue());
        }

        /** Verifica enmascaramiento de cuenta bancaria peruana. */
        @Test
        void deberiaEnmascararCuentaBancariaPeru() {
            MaskResult result = MaskEngine.mask("00212345678901234567", MaskType.BANK_ACCOUNT, CountryCode.PE);
            assertEquals("002*************4567", result.maskedValue());
        }

        /** Verifica enmascaramiento de nombre. */
        @Test
        void deberiaEnmascararNombre() {
            MaskResult result = MaskEngine.mask("Juan Perez", MaskType.PERSON_NAME, CountryCode.GENERIC);
            assertEquals("J*** P****", result.maskedValue());
        }

        /** Verifica enmascaramiento de IPv4. */
        @Test
        void deberiaEnmascararIpv4() {
            MaskResult result = MaskEngine.mask("192.168.1.100", MaskType.IP_ADDRESS, CountryCode.GENERIC);
            assertEquals("192.***.*.***", result.maskedValue());
        }
    }

    @Nested
    @DisplayName("API Builder")
    class ApiBuilder {

        /** Verifica que el builder produce el mismo resultado que la API estática. */
        @Test
        void builderDeberiaProducirMismoResultado() {
            MaskResult staticResult = MaskEngine.mask("john@gmail.com", MaskType.EMAIL, CountryCode.GENERIC);
            MaskResult builderResult = MaskEngine.builder()
                    .type(MaskType.EMAIL)
                    .country(CountryCode.GENERIC)
                    .mask("john@gmail.com");
            assertEquals(staticResult.maskedValue(), builderResult.maskedValue());
        }

        /** Verifica que el builder acepta maskChar personalizado. */
        @Test
        void builderDeberiaAceptarMaskCharPersonalizado() {
            MaskResult result = MaskEngine.builder()
                    .type(MaskType.PERSON_NAME)
                    .country(CountryCode.GENERIC)
                    .maskChar('#')
                    .mask("Juan Perez");
            assertEquals("J### P####", result.maskedValue());
        }
    }

    @Nested
    @DisplayName("Manejo de nulos y vacíos")
    class NulosYVacios {

        /** Null debería retornar MaskResult con null y wasMasked=false. */
        @Test
        void nullDeberiaRetornarMaskResultConNull() {
            MaskResult result = MaskEngine.mask(null, MaskType.EMAIL, CountryCode.GENERIC);
            assertNull(result.maskedValue());
            assertFalse(result.wasMasked());
        }

        /** Vacío debería retornar MaskResult con vacío y wasMasked=false. */
        @Test
        void vacioDeberiaRetornarMaskResultConVacio() {
            MaskResult result = MaskEngine.mask("", MaskType.EMAIL, CountryCode.GENERIC);
            assertEquals("", result.maskedValue());
            assertFalse(result.wasMasked());
        }
    }

    @Nested
    @DisplayName("Parameterized — Tabla de enmascaramiento")
    class ParameterizedTests {

        /** Verifica múltiples combinaciones de input/tipo/país/resultado. */
        @ParameterizedTest(name = "{0} [{1}, {2}] → {3}")
        @MethodSource("pe.edu.galaxy.training.java.libs.mask.utils.MaskEngineTest#maskTestCases")
        void deberiaEnmascararCorrectamente(String input, MaskType type, CountryCode country, String expected) {
            MaskResult result = MaskEngine.mask(input, type, country);
            assertEquals(expected, result.maskedValue());
        }
    }

    /** Provee casos de prueba para el test parameterizado. */
    static Stream<Arguments> maskTestCases() {
        return Stream.of(
                Arguments.of("12345678", MaskType.IDENTITY_DOCUMENT, CountryCode.PE, "*****678"),
                Arguments.of("123-45-6789", MaskType.IDENTITY_DOCUMENT, CountryCode.US, "***-**-6789"),
                Arguments.of("AB1234567", MaskType.IDENTITY_DOCUMENT, CountryCode.GENERIC, "AB*******"),
                Arguments.of("john@gmail.com", MaskType.EMAIL, CountryCode.GENERIC, "j***@gmail.com"),
                Arguments.of("Juan Perez", MaskType.PERSON_NAME, CountryCode.GENERIC, "J*** P****"),
                Arguments.of("192.168.1.100", MaskType.IP_ADDRESS, CountryCode.GENERIC, "192.***.*.***")
        );
    }
}
