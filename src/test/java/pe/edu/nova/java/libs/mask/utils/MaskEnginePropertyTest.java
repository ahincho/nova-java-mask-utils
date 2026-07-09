package pe.edu.nova.java.libs.mask.utils;

import net.jqwik.api.*;

import pe.edu.nova.java.libs.mask.utils.config.MaskConfig;
import pe.edu.nova.java.libs.mask.utils.generators.MaskTestGenerators;
import pe.edu.nova.java.libs.mask.utils.result.MaskResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property tests para {@link MaskEngine}.
 * Verifica propiedades P1, P2, P22, P23, P24 y P25.
 */
class MaskEnginePropertyTest {

    /**
     * P1: Confluencia Builder-Estático.
     * ∀ valor v, tipo t, país c, config cfg:
     * MaskEngine.mask(v, t, c, cfg) ≡ MaskEngine.builder().type(t).country(c).config(cfg).mask(v)
     * <p>
     * <b>Validates: Requirements 1.1, 1.2</b>
     */
    @Property(tries = 200)
    void confluenciaBuilderEstatico(
            @ForAll("validEmails") String email) {
        MaskConfig config = MaskConfig.defaults();
        MaskResult staticResult = MaskEngine.mask(email, MaskType.EMAIL, CountryCode.GENERIC, config);
        MaskResult builderResult = MaskEngine.builder()
                .type(MaskType.EMAIL)
                .country(CountryCode.GENERIC)
                .config(config)
                .mask(email);
        assertEquals(staticResult.maskedValue(), builderResult.maskedValue(),
                "Builder y estático deberían producir el mismo resultado");
    }

    /**
     * P2: Pasaje transparente de nulos y vacíos.
     * ∀ tipo t, país c: mask(null, t, c).maskedValue() = null ∧ mask("", t, c).maskedValue() = ""
     * <p>
     * <b>Validates: Requirements 1.4</b>
     */
    @Property(tries = 50)
    void pasajeTransparenteDeNulosYVacios(
            @ForAll("maskTypes") MaskType type,
            @ForAll("countryCodes") CountryCode country) {
        // Null
        MaskResult nullResult = MaskEngine.mask(null, type, country);
        assertNull(nullResult.maskedValue(), "Null debería retornar null");
        assertFalse(nullResult.wasMasked(), "Null no debería estar enmascarado");

        // Vacío
        MaskResult emptyResult = MaskEngine.mask("", type, country);
        assertEquals("", emptyResult.maskedValue(), "Vacío debería retornar vacío");
        assertFalse(emptyResult.wasMasked(), "Vacío no debería estar enmascarado");
    }

    /**
     * P22: Determinismo.
     * ∀ valor v, tipo t, país c: mask(v, t, c) = mask(v, t, c).
     * <p>
     * <b>Validates: Requirements 16.1</b>
     */
    @Property(tries = 200)
    void determinismo(
            @ForAll("validEmails") String email) {
        MaskResult r1 = MaskEngine.mask(email, MaskType.EMAIL, CountryCode.GENERIC);
        MaskResult r2 = MaskEngine.mask(email, MaskType.EMAIL, CountryCode.GENERIC);
        assertEquals(r1.maskedValue(), r2.maskedValue(),
                "Misma entrada debería producir mismo resultado");
    }

    /**
     * P23: Preservación de longitud.
     * ∀ valor v, tipo t, país c: length(mask(v, t, c).maskedValue()) = length(v).
     * <p>
     * <b>Validates: Requirements 16.2</b>
     */
    @Property(tries = 200)
    void preservacionDeLongitudEmail(
            @ForAll("validEmails") String email) {
        MaskResult result = MaskEngine.mask(email, MaskType.EMAIL, CountryCode.GENERIC);
        assertEquals(email.length(), result.maskedValue().length(),
                "Longitud debería preservarse");
    }

    /**
     * P23: Preservación de longitud para DNI peruano.
     */
    @Property(tries = 200)
    void preservacionDeLongitudDni(
            @ForAll("peruDni") String dni) {
        MaskResult result = MaskEngine.mask(dni, MaskType.IDENTITY_DOCUMENT, CountryCode.PE);
        assertEquals(dni.length(), result.maskedValue().length(),
                "Longitud debería preservarse para DNI");
    }

    /**
     * P24: Preservación de caracteres visibles.
     * ∀ email: el primer carácter del username y el dominio se preservan.
     * <p>
     * <b>Validates: Requirements 16.3</b>
     */
    @Property(tries = 200)
    void preservacionDeCaracteresVisiblesEmail(
            @ForAll("validEmails") String email) {
        MaskResult result = MaskEngine.mask(email, MaskType.EMAIL, CountryCode.GENERIC);
        String masked = result.maskedValue();

        int atIndex = email.indexOf('@');
        String domain = email.substring(atIndex);

        // Dominio preservado
        assertTrue(masked.endsWith(domain),
                "Dominio debería preservarse");

        // Primer carácter del username (si > 1 char)
        if (atIndex > 1) {
            assertEquals(email.charAt(0), masked.charAt(0),
                    "Primer carácter del username debería preservarse");
        }
    }

    /**
     * P25: Presencia de carácter de máscara.
     * ∀ valor v con length(v) > visibleChars: el resultado contiene al menos un '*'.
     * <p>
     * <b>Validates: Requirements 16.5</b>
     */
    @Property(tries = 200)
    void presenciaDeCaracterDeMascara(
            @ForAll("validEmails") String email) {
        // Emails con username > 1 char siempre tendrán caracteres enmascarados
        int atIndex = email.indexOf('@');
        if (atIndex > 1) {
            MaskResult result = MaskEngine.mask(email, MaskType.EMAIL, CountryCode.GENERIC);
            assertTrue(result.maskedValue().contains("*"),
                    "Debería contener al menos un carácter de máscara");
        }
    }

    @Provide
    Arbitrary<String> validEmails() {
        return MaskTestGenerators.validEmails();
    }

    @Provide
    Arbitrary<String> peruDni() {
        return MaskTestGenerators.peruDni();
    }

    @Provide
    Arbitrary<MaskType> maskTypes() {
        return MaskTestGenerators.randomMaskType();
    }

    @Provide
    Arbitrary<CountryCode> countryCodes() {
        return Arbitraries.of(CountryCode.values());
    }
}
