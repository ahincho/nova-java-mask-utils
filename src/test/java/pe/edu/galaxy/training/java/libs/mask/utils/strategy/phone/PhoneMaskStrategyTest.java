package pe.edu.galaxy.training.java.libs.mask.utils.strategy.phone;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import pe.edu.galaxy.training.java.libs.mask.utils.config.MaskConfig;
import pe.edu.galaxy.training.java.libs.mask.utils.result.MaskResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para estrategias de enmascaramiento de teléfonos.
 * Cubre PeruPhoneMaskStrategy, UsPhoneMaskStrategy y GenericPhoneMaskStrategy.
 */
@DisplayName("Estrategias de enmascaramiento de teléfonos")
class PhoneMaskStrategyTest {

    private final PeruPhoneMaskStrategy peruStrategy = new PeruPhoneMaskStrategy();
    private final UsPhoneMaskStrategy usStrategy = new UsPhoneMaskStrategy();
    private final GenericPhoneMaskStrategy genericStrategy = new GenericPhoneMaskStrategy();
    private final MaskConfig defaultConfig = MaskConfig.defaults();

    @Nested
    @DisplayName("Teléfono peruano")
    class TelefonoPeru {

        /** Verifica enmascaramiento estándar de teléfono peruano. */
        @Test
        void deberiaEnmascararTelefonoPeru() {
            MaskResult result = peruStrategy.mask("+51 987 654 321", defaultConfig);
            assertEquals("+51 *** *** 321", result.maskedValue());
        }

        /** Verifica que preserva el código de país +51. */
        @Test
        void deberiaPreservarCodigoPais() {
            String masked = peruStrategy.mask("+51 987 654 321", defaultConfig).maskedValue();
            assertTrue(masked.startsWith("+51"));
        }

        /** Verifica preservación de separadores (espacios). */
        @Test
        void deberiaPreservarEspacios() {
            String masked = peruStrategy.mask("+51 987 654 321", defaultConfig).maskedValue();
            assertEquals(' ', masked.charAt(3));
            assertEquals(' ', masked.charAt(7));
            assertEquals(' ', masked.charAt(11));
        }
    }

    @Nested
    @DisplayName("Teléfono estadounidense")
    class TelefonoUs {

        /** Verifica enmascaramiento estándar de teléfono US. */
        @Test
        void deberiaEnmascararTelefonoUs() {
            MaskResult result = usStrategy.mask("+1 555-123-4567", defaultConfig);
            assertEquals("+1 ***-***-4567", result.maskedValue());
        }

        /** Verifica que preserva el código de país +1. */
        @Test
        void deberiaPreservarCodigoPais() {
            String masked = usStrategy.mask("+1 555-123-4567", defaultConfig).maskedValue();
            assertTrue(masked.startsWith("+1"));
        }

        /** Verifica preservación de guiones. */
        @Test
        void deberiaPreservarGuiones() {
            String masked = usStrategy.mask("+1 555-123-4567", defaultConfig).maskedValue();
            assertEquals('-', masked.charAt(6));
            assertEquals('-', masked.charAt(10));
        }
    }

    @Nested
    @DisplayName("Teléfono genérico")
    class TelefonoGenerico {

        /** Verifica enmascaramiento genérico sin código de país. */
        @Test
        void deberiaEnmascararTelefonoGenerico() {
            MaskResult result = genericStrategy.mask("987654321", defaultConfig);
            assertEquals("******321", result.maskedValue());
        }

        /** Verifica preservación de separadores en genérico. */
        @Test
        void deberiaPreservarSeparadoresEnGenerico() {
            MaskResult result = genericStrategy.mask("987-654-321", defaultConfig);
            assertEquals("***-***-321", result.maskedValue());
        }
    }

    @Nested
    @DisplayName("Parameterized — Tabla de teléfonos")
    class ParameterizedTests {

        /** Verifica múltiples formatos de teléfonos peruanos. */
        @ParameterizedTest(name = "input={0}, expected={1}")
        @CsvSource({
                "+51 987 654 321, +51 *** *** 321",
                "+51 111 222 333, +51 *** *** 333",
                "+51 000 000 000, +51 *** *** 000"
        })
        void deberiaEnmascararTelefonoPeru(String input, String expected) {
            assertEquals(expected, peruStrategy.mask(input, defaultConfig).maskedValue());
        }

        /** Verifica múltiples formatos de teléfonos US. */
        @ParameterizedTest(name = "input={0}, expected={1}")
        @CsvSource({
                "+1 555-123-4567, +1 ***-***-4567",
                "+1 800-555-1234, +1 ***-***-1234",
                "+1 000-000-0000, +1 ***-***-0000"
        })
        void deberiaEnmascararTelefonoUs(String input, String expected) {
            assertEquals(expected, usStrategy.mask(input, defaultConfig).maskedValue());
        }
    }
}
