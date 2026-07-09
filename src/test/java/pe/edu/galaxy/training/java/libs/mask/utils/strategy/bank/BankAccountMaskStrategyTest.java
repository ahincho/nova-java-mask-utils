package pe.edu.galaxy.training.java.libs.mask.utils.strategy.bank;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import pe.edu.galaxy.training.java.libs.mask.utils.config.MaskConfig;
import pe.edu.galaxy.training.java.libs.mask.utils.result.MaskResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para estrategias de enmascaramiento de cuentas bancarias.
 * Cubre PeruBankAccountMaskStrategy, IbanMaskStrategy y GenericBankAccountMaskStrategy.
 */
@DisplayName("Estrategias de enmascaramiento de cuentas bancarias")
class BankAccountMaskStrategyTest {

    private final PeruBankAccountMaskStrategy peruStrategy = new PeruBankAccountMaskStrategy();
    private final IbanMaskStrategy ibanStrategy = new IbanMaskStrategy();
    private final GenericBankAccountMaskStrategy genericStrategy = new GenericBankAccountMaskStrategy();
    private final MaskConfig defaultConfig = MaskConfig.defaults();

    @Nested
    @DisplayName("CCI peruano")
    class CciPeru {

        /** Verifica enmascaramiento estándar de CCI de 20 dígitos. */
        @Test
        void deberiaEnmascararCciPeru() {
            MaskResult result = peruStrategy.mask("00212345678901234567", defaultConfig);
            assertEquals("002*************4567", result.maskedValue());
        }

        /** Verifica que muestra primeros 3 y últimos 4 dígitos. */
        @Test
        void deberiaMostrarPrimeros3YUltimos4() {
            String masked = peruStrategy.mask("00212345678901234567", defaultConfig).maskedValue();
            assertEquals("002", masked.substring(0, 3));
            assertEquals("4567", masked.substring(masked.length() - 4));
        }
    }

    @Nested
    @DisplayName("IBAN")
    class Iban {

        /** Verifica enmascaramiento estándar de IBAN con espacios. */
        @Test
        void deberiaEnmascararIbanConEspacios() {
            MaskResult result = ibanStrategy.mask("ES12 3456 7890 1234 5678", defaultConfig);
            assertEquals("ES12 **** **** **** 5678", result.maskedValue());
        }

        /** Verifica que preserva los primeros 4 caracteres (código país + control). */
        @Test
        void deberiaPreservarPrimeros4Caracteres() {
            String masked = ibanStrategy.mask("ES12 3456 7890 1234 5678", defaultConfig).maskedValue();
            assertEquals('E', masked.charAt(0));
            assertEquals('S', masked.charAt(1));
            assertEquals('1', masked.charAt(2));
            assertEquals('2', masked.charAt(3));
        }

        /** Verifica preservación de espacios. */
        @Test
        void deberiaPreservarEspacios() {
            String masked = ibanStrategy.mask("ES12 3456 7890 1234 5678", defaultConfig).maskedValue();
            assertEquals(' ', masked.charAt(4));
            assertEquals(' ', masked.charAt(9));
            assertEquals(' ', masked.charAt(14));
            assertEquals(' ', masked.charAt(19));
        }
    }

    @Nested
    @DisplayName("Genérico")
    class Generico {

        /** Verifica enmascaramiento genérico: primeros 3 + últimos 4. */
        @Test
        void deberiaEnmascararGenerico() {
            MaskResult result = genericStrategy.mask("1234567890", defaultConfig);
            assertEquals("123***7890", result.maskedValue());
        }
    }

    @Nested
    @DisplayName("Parameterized — Tabla de cuentas")
    class ParameterizedTests {

        /** Verifica múltiples CCI peruanos. */
        @ParameterizedTest(name = "input={0}, expected={1}")
        @CsvSource({
                "00212345678901234567, 002*************4567",
                "11100000000000000001, 111*************0001"
        })
        void deberiaEnmascararCciPeru(String input, String expected) {
            assertEquals(expected, peruStrategy.mask(input, defaultConfig).maskedValue());
        }

        /** Verifica múltiples IBAN. */
        @ParameterizedTest(name = "input={0}, expected={1}")
        @CsvSource({
                "ES12 3456 7890 1234 5678, ES12 **** **** **** 5678",
                "DE89370400440532013000, DE89**************3000"
        })
        void deberiaEnmascararIban(String input, String expected) {
            assertEquals(expected, ibanStrategy.mask(input, defaultConfig).maskedValue());
        }
    }
}
