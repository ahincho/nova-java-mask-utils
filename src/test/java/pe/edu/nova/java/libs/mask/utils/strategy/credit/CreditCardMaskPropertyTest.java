package pe.edu.nova.java.libs.mask.utils.strategy.credit;

import net.jqwik.api.*;

import pe.edu.nova.java.libs.mask.utils.CountryCode;
import pe.edu.nova.java.libs.mask.utils.config.MaskConfig;
import pe.edu.nova.java.libs.mask.utils.generators.MaskTestGenerators;
import pe.edu.nova.java.libs.mask.utils.result.MaskResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property tests para {@link CreditCardMaskStrategy}.
 * Verifica propiedades P12 y P9.
 */
class CreditCardMaskPropertyTest {

    private final CreditCardMaskStrategy strategy = new CreditCardMaskStrategy();

    /**
     * P12: Enmascaramiento de tarjetas de crédito PCI DSS.
     * ∀ tarjeta t con ≥ 13 dígitos: muestra primeros 6 (BIN) y últimos 4, enmascara intermedios.
     * <p>
     * <b>Validates: Requirements 7.1</b>
     */
    @Property(tries = 200)
    void deberiasMostrarBinYUltimosCuatroDigitos(
            @ForAll("validCreditCard") String card) {
        MaskConfig config = MaskConfig.builder().country(CountryCode.GENERIC).build();
        MaskResult result = strategy.mask(card, config);
        String masked = result.maskedValue();

        // Extraer solo dígitos del original y del enmascarado
        String originalDigits = card.replaceAll("[^0-9]", "");
        String maskedChars = masked.replaceAll("[^0-9*]", "");

        // Primeros 6 dígitos visibles
        assertEquals(originalDigits.substring(0, 6), maskedChars.substring(0, 6),
                "Primeros 6 dígitos (BIN) deberían ser visibles");

        // Últimos 4 dígitos visibles
        assertEquals(
                originalDigits.substring(originalDigits.length() - 4),
                maskedChars.substring(maskedChars.length() - 4),
                "Últimos 4 dígitos deberían ser visibles");

        // Dígitos intermedios enmascarados
        for (int i = 6; i < maskedChars.length() - 4; i++) {
            assertEquals('*', maskedChars.charAt(i),
                    "Dígito intermedio en posición " + i + " debería estar enmascarado");
        }
    }

    /**
     * P9: Preservación de separadores en tarjetas de crédito.
     * ∀ tarjeta con separadores: los separadores están en las mismas posiciones.
     * <p>
     * <b>Validates: Requirements 7.2</b>
     */
    @Property(tries = 200)
    void deberiaPreservarSeparadores(
            @ForAll("validCreditCard") String card) {
        MaskConfig config = MaskConfig.builder().country(CountryCode.GENERIC).build();
        MaskResult result = strategy.mask(card, config);
        String masked = result.maskedValue();

        assertEquals(card.length(), masked.length(), "Longitud debería preservarse");

        for (int i = 0; i < card.length(); i++) {
            char original = card.charAt(i);
            if (!Character.isDigit(original)) {
                assertEquals(original, masked.charAt(i),
                        "Separador en posición " + i + " debería preservarse");
            }
        }
    }

    @Provide
    Arbitrary<String> validCreditCard() {
        return MaskTestGenerators.validCreditCard();
    }
}
