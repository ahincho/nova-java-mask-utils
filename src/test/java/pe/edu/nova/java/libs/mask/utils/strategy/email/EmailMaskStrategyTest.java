package pe.edu.nova.java.libs.mask.utils.strategy.email;

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
 * Tests unitarios para {@link EmailMaskStrategy}.
 * Verifica enmascaramiento de emails, BVA y manejo de errores.
 */
@DisplayName("EmailMaskStrategy — Enmascaramiento de emails")
class EmailMaskStrategyTest {

    private final EmailMaskStrategy strategy = new EmailMaskStrategy();
    private final MaskConfig defaultConfig = MaskConfig.defaults();

    @Nested
    @DisplayName("Casos estándar")
    class CasosEstandar {

        /** Verifica enmascaramiento estándar de email. */
        @Test
        void deberiaEnmascararEmailEstandar() {
            MaskResult result = strategy.mask("john.doe@gmail.com", defaultConfig);
            assertEquals("j*******@gmail.com", result.maskedValue());
        }

        /** Verifica que preserva el dominio completo. */
        @Test
        void deberiaPreservarDominio() {
            String masked = strategy.mask("user@example.com", defaultConfig).maskedValue();
            assertTrue(masked.endsWith("@example.com"));
        }

        /** Verifica que muestra el primer carácter del username. */
        @Test
        void deberiaMostrarPrimerCaracterDelUsername() {
            String masked = strategy.mask("john@test.com", defaultConfig).maskedValue();
            assertEquals('j', masked.charAt(0));
        }
    }

    @Nested
    @DisplayName("BVA — Username de 1 y 2 caracteres")
    class BoundaryValueAnalysis {

        /** Username de 1 carácter: completamente enmascarado. */
        @Test
        void usernameUnCaracterDeberiaEnmascararse() {
            MaskResult result = strategy.mask("j@gmail.com", defaultConfig);
            assertEquals("*@gmail.com", result.maskedValue());
        }

        /** Username de 2 caracteres: primer carácter visible, segundo enmascarado. */
        @Test
        void usernameDosCaracteresDeberiaMostrarPrimero() {
            MaskResult result = strategy.mask("jo@gmail.com", defaultConfig);
            assertEquals("j*@gmail.com", result.maskedValue());
        }
    }

    @Nested
    @DisplayName("Negativos — Formatos inválidos")
    class Negativos {

        /** Sin @ debería lanzar InvalidFormatException. */
        @Test
        void sinArrobaDeberiaLanzarExcepcion() {
            InvalidFormatException ex = assertThrows(InvalidFormatException.class,
                    () -> strategy.mask("invalid-email", defaultConfig));
            assertEquals(MaskType.EMAIL, ex.getMaskType());
            assertNotNull(ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Parameterized — Tabla de emails")
    class ParameterizedTests {

        /** Verifica múltiples emails con @CsvSource. */
        @ParameterizedTest(name = "input={0}, expected={1}")
        @CsvSource({
                "john.doe@gmail.com, j*******@gmail.com",
                "a@test.com, *@test.com",
                "ab@test.com, a*@test.com",
                "abc@test.com, a**@test.com",
                "user123@company.pe, u******@company.pe"
        })
        void deberiaEnmascararEmail(String input, String expected) {
            assertEquals(expected, strategy.mask(input, defaultConfig).maskedValue());
        }
    }
}
