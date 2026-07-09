package pe.edu.galaxy.training.java.libs.mask.utils.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.MaskType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link MaskConfig}.
 * Verifica valores por defecto, builder, forType() y validación de parámetros.
 */
@DisplayName("MaskConfig — Configuración de enmascaramiento")
class MaskConfigTest {

    @Nested
    @DisplayName("Valores por defecto")
    class Defaults {

        /** Verifica que defaults() retorna maskChar='*'. */
        @Test
        void deberiaRetornarMaskCharPorDefecto() {
            MaskConfig config = MaskConfig.defaults();
            assertEquals('*', config.maskChar());
        }

        /** Verifica que defaults() retorna visibleStart=0. */
        @Test
        void deberiaRetornarVisibleStartCero() {
            MaskConfig config = MaskConfig.defaults();
            assertEquals(0, config.visibleStart());
        }

        /** Verifica que defaults() retorna visibleEnd=0. */
        @Test
        void deberiaRetornarVisibleEndCero() {
            MaskConfig config = MaskConfig.defaults();
            assertEquals(0, config.visibleEnd());
        }

        /** Verifica que defaults() retorna un country no nulo. */
        @Test
        void deberiaRetornarCountryNoNulo() {
            MaskConfig config = MaskConfig.defaults();
            assertNotNull(config.country());
        }
    }

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        /** Verifica que el builder configura maskChar correctamente. */
        @Test
        void deberiaConfigurarMaskChar() {
            MaskConfig config = MaskConfig.builder().maskChar('#').build();
            assertEquals('#', config.maskChar());
        }

        /** Verifica que el builder configura visibleStart correctamente. */
        @Test
        void deberiaConfigurarVisibleStart() {
            MaskConfig config = MaskConfig.builder().visibleStart(3).build();
            assertEquals(3, config.visibleStart());
        }

        /** Verifica que el builder configura visibleEnd correctamente. */
        @Test
        void deberiaConfigurarVisibleEnd() {
            MaskConfig config = MaskConfig.builder().visibleEnd(4).build();
            assertEquals(4, config.visibleEnd());
        }

        /** Verifica que el builder configura country correctamente. */
        @Test
        void deberiaConfigurarCountry() {
            MaskConfig config = MaskConfig.builder().country(CountryCode.PE).build();
            assertEquals(CountryCode.PE, config.country());
        }

        /** Verifica que el builder permite encadenar todas las configuraciones. */
        @Test
        void deberiaPermitirEncadenamiento() {
            MaskConfig config = MaskConfig.builder()
                    .maskChar('X')
                    .visibleStart(2)
                    .visibleEnd(3)
                    .country(CountryCode.US)
                    .build();
            assertEquals('X', config.maskChar());
            assertEquals(2, config.visibleStart());
            assertEquals(3, config.visibleEnd());
            assertEquals(CountryCode.US, config.country());
        }
    }

    @Nested
    @DisplayName("forType() — Valores predeterminados por tipo")
    class ForTypeTests {

        /** Verifica configuración predeterminada para EMAIL. */
        @Test
        void deberiaRetornarConfigParaEmail() {
            MaskConfig config = MaskConfig.forType(MaskType.EMAIL);
            assertEquals(1, config.visibleStart());
            assertEquals(0, config.visibleEnd());
        }

        /** Verifica configuración predeterminada para PHONE. */
        @Test
        void deberiaRetornarConfigParaPhone() {
            MaskConfig config = MaskConfig.forType(MaskType.PHONE);
            assertEquals(0, config.visibleStart());
            assertEquals(3, config.visibleEnd());
        }

        /** Verifica configuración predeterminada para IDENTITY_DOCUMENT. */
        @Test
        void deberiaRetornarConfigParaIdentityDocument() {
            MaskConfig config = MaskConfig.forType(MaskType.IDENTITY_DOCUMENT);
            assertEquals(0, config.visibleStart());
            assertEquals(3, config.visibleEnd());
        }

        /** Verifica configuración predeterminada para CREDIT_CARD. */
        @Test
        void deberiaRetornarConfigParaCreditCard() {
            MaskConfig config = MaskConfig.forType(MaskType.CREDIT_CARD);
            assertEquals(6, config.visibleStart());
            assertEquals(4, config.visibleEnd());
        }

        /** Verifica configuración predeterminada para BANK_ACCOUNT. */
        @Test
        void deberiaRetornarConfigParaBankAccount() {
            MaskConfig config = MaskConfig.forType(MaskType.BANK_ACCOUNT);
            assertEquals(3, config.visibleStart());
            assertEquals(4, config.visibleEnd());
        }

        /** Verifica configuración predeterminada para PERSON_NAME. */
        @Test
        void deberiaRetornarConfigParaPersonName() {
            MaskConfig config = MaskConfig.forType(MaskType.PERSON_NAME);
            assertEquals(1, config.visibleStart());
            assertEquals(0, config.visibleEnd());
        }

        /** Verifica configuración predeterminada para IP_ADDRESS. */
        @Test
        void deberiaRetornarConfigParaIpAddress() {
            MaskConfig config = MaskConfig.forType(MaskType.IP_ADDRESS);
            assertEquals(1, config.visibleStart());
            assertEquals(0, config.visibleEnd());
        }
    }

    @Nested
    @DisplayName("BVA — Análisis de valores límite")
    class BoundaryValueAnalysis {

        /** Verifica que visibleStart=0 es válido. */
        @Test
        void deberiaAceptarVisibleStartCero() {
            MaskConfig config = MaskConfig.builder().visibleStart(0).build();
            assertEquals(0, config.visibleStart());
        }

        /** Verifica que visibleEnd=0 es válido. */
        @Test
        void deberiaAceptarVisibleEndCero() {
            MaskConfig config = MaskConfig.builder().visibleEnd(0).build();
            assertEquals(0, config.visibleEnd());
        }

        /** Verifica que visibleStart=-1 lanza IllegalArgumentException. */
        @Test
        void deberiaLanzarExcepcionParaVisibleStartNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> MaskConfig.builder().visibleStart(-1));
        }

        /** Verifica que visibleEnd=-1 lanza IllegalArgumentException. */
        @Test
        void deberiaLanzarExcepcionParaVisibleEndNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> MaskConfig.builder().visibleEnd(-1));
        }
    }

    @Nested
    @DisplayName("Negativos — Manejo de valores inválidos")
    class NegativeTests {

        /** Verifica que maskChar '\0' (null char) usa '*' por defecto. */
        @Test
        void deberiaUsarDefaultParaNullChar() {
            MaskConfig config = MaskConfig.builder().maskChar('\0').build();
            assertEquals('*', config.maskChar());
        }
    }
}
