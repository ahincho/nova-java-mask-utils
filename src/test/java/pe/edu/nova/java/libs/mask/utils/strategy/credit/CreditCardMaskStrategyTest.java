package pe.edu.nova.java.libs.mask.utils.strategy.credit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import pe.edu.nova.java.libs.mask.utils.MaskType;
import pe.edu.nova.java.libs.mask.utils.config.MaskConfig;
import pe.edu.nova.java.libs.mask.utils.exception.InvalidFormatException;
import pe.edu.nova.java.libs.mask.utils.result.MaskResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link CreditCardMaskStrategy}.
 * Verifica enmascaramiento PCI DSS, BVA y manejo de errores.
 */
@DisplayName("CreditCardMaskStrategy — Enmascaramiento de tarjetas de crédito")
class CreditCardMaskStrategyTest {

    private final CreditCardMaskStrategy strategy = new CreditCardMaskStrategy();
    private final MaskConfig defaultConfig = MaskConfig.defaults();

    @Nested
    @DisplayName("Casos estándar")
    class CasosEstandar {

        /** Verifica enmascaramiento estándar con espacios. */
        @Test
        void deberiaEnmascararTarjetaConEspacios() {
            MaskResult result = strategy.mask("4532 0123 4567 8901", defaultConfig);
            assertEquals("4532 01** **** 8901", result.maskedValue());
        }

        /** Verifica enmascaramiento sin separadores. */
        @Test
        void deberiaEnmascararTarjetaSinSeparadores() {
            MaskResult result = strategy.mask("4532012345678901", defaultConfig);
            assertEquals("4532 01** **** 8901".replace(" ", ""),
                    result.maskedValue().replace(" ", ""));
            // Sin espacios: "453201******8901"
            assertEquals("453201******8901", result.maskedValue());
        }

        /** Verifica que preserva separadores. */
        @Test
        void deberiaPreservarSeparadores() {
            String masked = strategy.mask("4532 0123 4567 8901", defaultConfig).maskedValue();
            assertEquals(' ', masked.charAt(4));
            assertEquals(' ', masked.charAt(9));
            assertEquals(' ', masked.charAt(14));
        }
    }

    @Nested
    @DisplayName("BVA — Análisis de valores límite")
    class BoundaryValueAnalysis {

        /** 12 dígitos (inválido) — debería lanzar InvalidFormatException. */
        @Test
        void doceDigitosDeberiaLanzarExcepcion() {
            assertThrows(InvalidFormatException.class,
                    () -> strategy.mask("123456789012", defaultConfig));
        }

        /** 13 dígitos (mínimo válido) — debería enmascarar correctamente. */
        @Test
        void treceDigitosDeberiaEnmascarar() {
            MaskResult result = strategy.mask("1234567890123", defaultConfig);
            assertTrue(result.wasMasked());
            // Primeros 6 visibles, últimos 4 visibles, 3 enmascarados
            assertEquals("123456***0123", result.maskedValue());
        }

        /** 19 dígitos (máximo típico) — debería enmascarar correctamente. */
        @Test
        void diecinueveDigitosDeberiaEnmascarar() {
            MaskResult result = strategy.mask("1234567890123456789", defaultConfig);
            assertTrue(result.wasMasked());
            String masked = result.maskedValue();
            // Primeros 6 visibles
            assertEquals("123456", masked.substring(0, 6));
            // Últimos 4 visibles
            assertEquals("6789", masked.substring(masked.length() - 4));
        }
    }

    @Nested
    @DisplayName("Negativos — Formatos inválidos")
    class Negativos {

        /** Menos de 13 dígitos debería lanzar InvalidFormatException. */
        @Test
        void menosDeTreceDigitosDeberiaLanzarExcepcion() {
            InvalidFormatException ex = assertThrows(InvalidFormatException.class,
                    () -> strategy.mask("1234 5678 9012", defaultConfig));
            assertEquals(MaskType.CREDIT_CARD, ex.getMaskType());
        }
    }

    @Nested
    @DisplayName("Parameterized — Tabla de tarjetas")
    class ParameterizedTests {

        /** Verifica múltiples formatos de tarjetas. */
        @ParameterizedTest(name = "input={0}, expected={1}")
        @CsvSource({
                "4532012345678901, 453201******8901",
                "4532 0123 4567 8901, 4532 01** **** 8901"
        })
        void deberiaEnmascararTarjeta(String input, String expected) {
            assertEquals(expected, strategy.mask(input, defaultConfig).maskedValue());
        }
    }
}
