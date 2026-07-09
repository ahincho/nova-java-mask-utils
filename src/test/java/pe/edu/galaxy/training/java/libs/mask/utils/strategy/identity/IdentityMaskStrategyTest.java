package pe.edu.galaxy.training.java.libs.mask.utils.strategy.identity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.MaskType;
import pe.edu.galaxy.training.java.libs.mask.utils.config.MaskConfig;
import pe.edu.galaxy.training.java.libs.mask.utils.result.MaskResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para estrategias de enmascaramiento de documentos de identidad.
 * Cubre PeruIdentityMaskStrategy, UsIdentityMaskStrategy y GenericIdentityMaskStrategy.
 */
@DisplayName("Estrategias de enmascaramiento de documentos de identidad")
class IdentityMaskStrategyTest {

    private final PeruIdentityMaskStrategy peruStrategy = new PeruIdentityMaskStrategy();
    private final UsIdentityMaskStrategy usStrategy = new UsIdentityMaskStrategy();
    private final GenericIdentityMaskStrategy genericStrategy = new GenericIdentityMaskStrategy();
    private final MaskConfig defaultConfig = MaskConfig.defaults();

    @Nested
    @DisplayName("DNI peruano")
    class PeruDni {

        /** Verifica enmascaramiento estándar de DNI peruano de 8 dígitos. */
        @Test
        void deberiaEnmascararDniPeru() {
            MaskResult result = peruStrategy.mask("12345678", defaultConfig);
            assertEquals("*****678", result.maskedValue());
            assertTrue(result.wasMasked());
        }

        /** Verifica que el tipo de resultado es IDENTITY_DOCUMENT. */
        @Test
        void deberiaRetornarTipoIdentityDocument() {
            MaskResult result = peruStrategy.mask("12345678", defaultConfig);
            assertEquals(MaskType.IDENTITY_DOCUMENT, result.maskType());
        }
    }

    @Nested
    @DisplayName("SSN estadounidense")
    class UsSsn {

        /** Verifica enmascaramiento estándar de SSN con guiones. */
        @Test
        void deberiaEnmascararSsnConGuiones() {
            MaskResult result = usStrategy.mask("123-45-6789", defaultConfig);
            assertEquals("***-**-6789", result.maskedValue());
        }

        /** Verifica que preserva los guiones en sus posiciones originales. */
        @Test
        void deberiaPreservarGuiones() {
            String masked = usStrategy.mask("123-45-6789", defaultConfig).maskedValue();
            assertEquals('-', masked.charAt(3));
            assertEquals('-', masked.charAt(6));
        }
    }

    @Nested
    @DisplayName("Pasaporte genérico")
    class PasaporteGenerico {

        /** Verifica enmascaramiento de pasaporte genérico. */
        @Test
        void deberiaEnmascararPasaporteGenerico() {
            MaskResult result = genericStrategy.mask("AB1234567", defaultConfig);
            assertEquals("AB*******", result.maskedValue());
        }

        /** Verifica que valores cortos (≤2) se enmascaran completamente. */
        @Test
        void deberiaEnmascararCompletamenteValoresCortos() {
            assertEquals("**", genericStrategy.mask("AB", defaultConfig).maskedValue());
            assertEquals("*", genericStrategy.mask("A", defaultConfig).maskedValue());
        }
    }

    @Nested
    @DisplayName("BVA — Análisis de valores límite")
    class BoundaryValueAnalysis {

        /** Longitud 0 — cadena vacía no llega a la estrategia (MaskEngine la maneja). */
        @Test
        void longitudCeroDeberiaRetornarVacio() {
            MaskResult result = peruStrategy.mask("", defaultConfig);
            assertEquals("", result.maskedValue());
        }

        /** Longitud 1 — completamente enmascarado para Peru (visible end = 3 > length). */
        @Test
        void longitudUnoDeberiaRetornarCaracterOriginal() {
            MaskResult result = peruStrategy.mask("1", defaultConfig);
            assertEquals("1", result.maskedValue());
        }

        /** Longitud 2 — completamente visible para Peru (visible end = 3 > length). */
        @Test
        void longitudDosDeberiaRetornarCaracteresOriginales() {
            MaskResult result = peruStrategy.mask("12", defaultConfig);
            assertEquals("12", result.maskedValue());
        }

        /** Longitud 3 — exactamente los caracteres visibles para Peru. */
        @Test
        void longitudTresDeberiaRetornarTodoVisible() {
            MaskResult result = peruStrategy.mask("123", defaultConfig);
            assertEquals("123", result.maskedValue());
        }

        /** Longitud 7 — un dígito menos que DNI estándar. */
        @Test
        void longitudSieteDeberiaEnmascarar() {
            MaskResult result = peruStrategy.mask("1234567", defaultConfig);
            assertEquals("****567", result.maskedValue());
        }

        /** Longitud 8 — DNI estándar. */
        @Test
        void longitudOchoDeberiaEnmascarar() {
            MaskResult result = peruStrategy.mask("12345678", defaultConfig);
            assertEquals("*****678", result.maskedValue());
        }

        /** Longitud 9 — un dígito más que DNI estándar. */
        @Test
        void longitudNueveDeberiaEnmascarar() {
            MaskResult result = peruStrategy.mask("123456789", defaultConfig);
            assertEquals("******789", result.maskedValue());
        }
    }

    @Nested
    @DisplayName("Parameterized — Tabla de documentos")
    class ParameterizedTests {

        /** Verifica múltiples combinaciones de input/país/resultado esperado. */
        @ParameterizedTest(name = "input={0}, expected={1}")
        @CsvSource({
                "12345678, *****678",
                "00000000, *****000",
                "99999999, *****999",
                "1234, *234"
        })
        void deberiaEnmascararDniPeru(String input, String expected) {
            assertEquals(expected, peruStrategy.mask(input, defaultConfig).maskedValue());
        }

        /** Verifica múltiples SSN estadounidenses. */
        @ParameterizedTest(name = "input={0}, expected={1}")
        @CsvSource({
                "123-45-6789, ***-**-6789",
                "000-00-0000, ***-**-0000",
                "999-99-9999, ***-**-9999"
        })
        void deberiaEnmascararSsnUs(String input, String expected) {
            assertEquals(expected, usStrategy.mask(input, defaultConfig).maskedValue());
        }

        /** Verifica múltiples pasaportes genéricos. */
        @ParameterizedTest(name = "input={0}, expected={1}")
        @CsvSource({
                "AB1234567, AB*******",
                "XY9876, XY****",
                "A, *",
                "AB, **"
        })
        void deberiaEnmascararPasaporteGenerico(String input, String expected) {
            assertEquals(expected, genericStrategy.mask(input, defaultConfig).maskedValue());
        }
    }
}
