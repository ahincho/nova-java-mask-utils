package pe.edu.galaxy.training.java.libs.mask.utils.strategy.identity;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.MaskType;
import pe.edu.galaxy.training.java.libs.mask.utils.config.MaskConfig;
import pe.edu.galaxy.training.java.libs.mask.utils.result.MaskResult;
import pe.edu.galaxy.training.java.libs.mask.utils.strategy.MaskStrategy;

/**
 * Estrategia de enmascaramiento para SSN estadounidense (formato XXX-XX-XXXX).
 * <p>
 * Enmascara todos los dígitos excepto los últimos 4, preservando guiones
 * en sus posiciones originales.
 * Ejemplo: "123-45-6789" → "***-**-6789"
 */
public class UsIdentityMaskStrategy implements MaskStrategy {

    /** Crea una nueva instancia de {@code UsIdentityMaskStrategy}. */
    public UsIdentityMaskStrategy() {}

    private static final int VISIBLE_END_DIGITS = 4;

    @Override
    public MaskResult mask(String value, MaskConfig config) {
        char maskChar = config.maskChar();

        // Contar dígitos totales para determinar cuáles son visibles
        int totalDigits = 0;
        for (int i = 0; i < value.length(); i++) {
            if (Character.isDigit(value.charAt(i))) {
                totalDigits++;
            }
        }

        int digitsToMask = totalDigits - VISIBLE_END_DIGITS;
        StringBuilder masked = new StringBuilder(value.length());
        int digitIndex = 0;

        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (Character.isDigit(ch)) {
                if (digitIndex < digitsToMask) {
                    masked.append(maskChar);
                } else {
                    masked.append(ch);
                }
                digitIndex++;
            } else {
                // Preservar separadores (guiones, espacios, etc.)
                masked.append(ch);
            }
        }

        return new MaskResult(
                value,
                masked.toString(),
                MaskType.IDENTITY_DOCUMENT,
                CountryCode.US,
                true
        );
    }
}
