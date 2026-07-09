package pe.edu.nova.java.libs.mask.utils.strategy.phone;

import net.jqwik.api.*;

import pe.edu.nova.java.libs.mask.utils.config.MaskConfig;
import pe.edu.nova.java.libs.mask.utils.generators.MaskTestGenerators;
import pe.edu.nova.java.libs.mask.utils.result.MaskResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property tests para estrategias de enmascaramiento de teléfonos.
 * Verifica propiedades P8 y P9.
 */
class PhoneMaskPropertyTest {

    private final PeruPhoneMaskStrategy peruStrategy = new PeruPhoneMaskStrategy();
    private final UsPhoneMaskStrategy usStrategy = new UsPhoneMaskStrategy();
    private final MaskConfig defaultConfig = MaskConfig.defaults();

    /**
     * P8: Enmascaramiento de teléfonos peruanos.
     * ∀ teléfono peruano p (formato +51 XXX XXX XXX): preserva +51, muestra últimos 3.
     * <p>
     * <b>Validates: Requirements 5.1, 5.2, 5.4</b>
     */
    @Property(tries = 200)
    void telefonoPeruanoDeberiaPreservarCodigoPaisYMostrarUltimos3(
            @ForAll("peruPhone") String phone) {
        MaskResult result = peruStrategy.mask(phone, defaultConfig);
        String masked = result.maskedValue();

        // Preserva código de país
        assertTrue(masked.startsWith("+51"), "Debería preservar +51");
        // Longitud preservada
        assertEquals(phone.length(), masked.length());
        // Últimos 3 dígitos visibles
        String originalDigits = phone.replaceAll("[^0-9]", "");
        String maskedDigits = masked.replaceAll("[^0-9*]", "");
        String lastThreeOriginal = originalDigits.substring(originalDigits.length() - 3);
        assertTrue(masked.endsWith(lastThreeOriginal),
                "Debería terminar con los últimos 3 dígitos: " + lastThreeOriginal);
    }

    /**
     * P8: Enmascaramiento de teléfonos US.
     * ∀ teléfono US p (formato +1 XXX-XXX-XXXX): preserva +1, muestra últimos 4.
     * <p>
     * <b>Validates: Requirements 5.1, 5.2, 5.4</b>
     */
    @Property(tries = 200)
    void telefonoUsDeberiaPreservarCodigoPaisYMostrarUltimos4(
            @ForAll("usPhone") String phone) {
        MaskResult result = usStrategy.mask(phone, defaultConfig);
        String masked = result.maskedValue();

        // Preserva código de país
        assertTrue(masked.startsWith("+1"), "Debería preservar +1");
        // Longitud preservada
        assertEquals(phone.length(), masked.length());
        // Últimos 4 dígitos visibles
        String originalDigits = phone.replaceAll("[^0-9]", "");
        String lastFourOriginal = originalDigits.substring(originalDigits.length() - 4);
        assertTrue(masked.endsWith(lastFourOriginal),
                "Debería terminar con los últimos 4 dígitos: " + lastFourOriginal);
    }

    /**
     * P9: Preservación de separadores en teléfonos peruanos.
     * ∀ teléfono con separadores: los separadores están en las mismas posiciones.
     * <p>
     * <b>Validates: Requirements 5.5</b>
     */
    @Property(tries = 200)
    void deberiaPreservarSeparadoresEnTelefonoPeru(
            @ForAll("peruPhone") String phone) {
        MaskResult result = peruStrategy.mask(phone, defaultConfig);
        String masked = result.maskedValue();

        for (int i = 0; i < phone.length(); i++) {
            char original = phone.charAt(i);
            if (!Character.isDigit(original) && original != '+') {
                assertEquals(original, masked.charAt(i),
                        "Separador en posición " + i + " debería preservarse");
            }
        }
    }

    @Provide
    Arbitrary<String> peruPhone() {
        return MaskTestGenerators.peruPhone();
    }

    @Provide
    Arbitrary<String> usPhone() {
        return MaskTestGenerators.usPhone();
    }
}
