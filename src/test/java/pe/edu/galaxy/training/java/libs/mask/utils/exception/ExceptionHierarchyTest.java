package pe.edu.galaxy.training.java.libs.mask.utils.exception;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Combinators;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.MaskType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para la jerarquía de excepciones de mask-utils.
 * Verifica herencia, campos accesibles y propiedad P21.
 */
@DisplayName("Jerarquía de excepciones")
class ExceptionHierarchyTest {

    @Nested
    @DisplayName("Herencia")
    class Herencia {

        /** Verifica que MaskException extiende RuntimeException. */
        @Test
        void maskExceptionDeberiaExtenderRuntimeException() {
            MaskException ex = new MaskException("test", MaskType.EMAIL, CountryCode.GENERIC);
            assertInstanceOf(RuntimeException.class, ex);
        }

        /** Verifica que UnsupportedMaskTypeException extiende MaskException. */
        @Test
        void unsupportedMaskTypeExceptionDeberiaExtenderMaskException() {
            UnsupportedMaskTypeException ex = new UnsupportedMaskTypeException(
                    "test", MaskType.EMAIL, CountryCode.GENERIC);
            assertInstanceOf(MaskException.class, ex);
        }

        /** Verifica que InvalidFormatException extiende MaskException. */
        @Test
        void invalidFormatExceptionDeberiaExtenderMaskException() {
            InvalidFormatException ex = new InvalidFormatException(
                    "test", MaskType.EMAIL, CountryCode.GENERIC);
            assertInstanceOf(MaskException.class, ex);
        }
    }

    @Nested
    @DisplayName("Campos accesibles")
    class CamposAccesibles {

        /** Verifica que MaskException expone maskType. */
        @Test
        void deberiaExponerMaskType() {
            MaskException ex = new MaskException("error", MaskType.PHONE, CountryCode.PE);
            assertEquals(MaskType.PHONE, ex.getMaskType());
        }

        /** Verifica que MaskException expone countryCode. */
        @Test
        void deberiaExponerCountryCode() {
            MaskException ex = new MaskException("error", MaskType.PHONE, CountryCode.PE);
            assertEquals(CountryCode.PE, ex.getCountryCode());
        }

        /** Verifica que MaskException expone message. */
        @Test
        void deberiaExponerMessage() {
            MaskException ex = new MaskException("error descriptivo", MaskType.PHONE, CountryCode.PE);
            assertEquals("error descriptivo", ex.getMessage());
        }

        /** Verifica que MaskException con causa expone la causa. */
        @Test
        void deberiaExponerCause() {
            RuntimeException cause = new RuntimeException("causa original");
            MaskException ex = new MaskException("error", MaskType.EMAIL, CountryCode.US, cause);
            assertEquals(cause, ex.getCause());
        }
    }

    /**
     * Propiedad P21: Contexto en excepciones.
     * ∀ excepción de tipo MaskException: getMaskType() ≠ null ∧ getCountryCode() ≠ null ∧ getMessage() ≠ null.
     * <p>
     * <b>Validates: Requirements 15.5</b>
     */
    @Property(tries = 100)
    void contextoEnExcepcionesDeberiaSerNoNulo(
            @ForAll("maskTypes") MaskType type,
            @ForAll("countryCodes") CountryCode country) {
        MaskException ex = new MaskException("mensaje de prueba", type, country);
        assertNotNull(ex.getMaskType(), "maskType no debería ser nulo");
        assertNotNull(ex.getCountryCode(), "countryCode no debería ser nulo");
        assertNotNull(ex.getMessage(), "message no debería ser nulo");
    }

    @Provide
    Arbitrary<MaskType> maskTypes() {
        return Arbitraries.of(MaskType.values());
    }

    @Provide
    Arbitrary<CountryCode> countryCodes() {
        return Arbitraries.of(CountryCode.values());
    }
}
