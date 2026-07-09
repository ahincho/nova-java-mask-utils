package pe.edu.galaxy.training.java.libs.mask.utils.strategy.name;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import pe.edu.galaxy.training.java.libs.mask.utils.config.MaskConfig;
import pe.edu.galaxy.training.java.libs.mask.utils.result.MaskResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link PersonNameMaskStrategy}.
 * Verifica enmascaramiento de nombres, BVA y casos especiales.
 */
@DisplayName("PersonNameMaskStrategy — Enmascaramiento de nombres")
class PersonNameMaskStrategyTest {

    private final PersonNameMaskStrategy strategy = new PersonNameMaskStrategy();
    private final MaskConfig defaultConfig = MaskConfig.defaults();

    @Nested
    @DisplayName("Casos estándar")
    class CasosEstandar {

        /** Verifica enmascaramiento de nombre completo. */
        @Test
        void deberiaEnmascararNombreCompleto() {
            MaskResult result = strategy.mask("Juan Perez", defaultConfig);
            assertEquals("J*** P****", result.maskedValue());
        }

        /** Verifica enmascaramiento de nombre simple. */
        @Test
        void deberiaEnmascararNombreSimple() {
            MaskResult result = strategy.mask("Juan", defaultConfig);
            assertEquals("J***", result.maskedValue());
        }
    }

    @Nested
    @DisplayName("BVA — Análisis de valores límite")
    class BoundaryValueAnalysis {

        /** Nombre vacío debería retornar cadena vacía. */
        @Test
        void nombreVacioDeberiaRetornarVacio() {
            MaskResult result = strategy.mask("", defaultConfig);
            assertEquals("", result.maskedValue());
        }

        /** Solo espacios debería retornar cadena vacía. */
        @Test
        void soloEspaciosDeberiaRetornarVacio() {
            MaskResult result = strategy.mask("   ", defaultConfig);
            assertEquals("", result.maskedValue());
        }

        /** Una sola palabra. */
        @Test
        void unaPalabraDeberiaEnmascarar() {
            assertEquals("J***", strategy.mask("Juan", defaultConfig).maskedValue());
        }

        /** Cinco palabras. */
        @Test
        void cincoPalabrasDeberiaEnmascarar() {
            MaskResult result = strategy.mask("Juan Carlos Perez Lopez Garcia", defaultConfig);
            assertEquals("J*** C***** P**** L**** G*****", result.maskedValue());
        }
    }

    @Nested
    @DisplayName("Parameterized — Tabla de nombres")
    class ParameterizedTests {

        /** Verifica múltiples nombres. */
        @ParameterizedTest(name = "input={0}, expected={1}")
        @CsvSource({
                "Juan Perez, J*** P****",
                "Juan, J***",
                "Ana Maria Lopez, A** M**** L****",
                "A, A"
        })
        void deberiaEnmascararNombre(String input, String expected) {
            assertEquals(expected, strategy.mask(input, defaultConfig).maskedValue());
        }
    }
}
