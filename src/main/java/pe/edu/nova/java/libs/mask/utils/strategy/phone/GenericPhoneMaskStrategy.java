package pe.edu.nova.java.libs.mask.utils.strategy.phone;

import pe.edu.nova.java.libs.mask.utils.CountryCode;
import pe.edu.nova.java.libs.mask.utils.MaskType;
import pe.edu.nova.java.libs.mask.utils.config.MaskConfig;
import pe.edu.nova.java.libs.mask.utils.result.MaskResult;
import pe.edu.nova.java.libs.mask.utils.strategy.MaskStrategy;

/**
 * Estrategia de enmascaramiento genérica para teléfonos (Sin código de país).
 * <p>
 * Enmascara todos los dígitos excepto los últimos 3, preservando separadores
 * en sus posiciones originales.
 * Ejemplo: "987654321" → "******321"
 * Ejemplo: "987-654-321" → "***-***-321"
 */
public class GenericPhoneMaskStrategy implements MaskStrategy {

    /** Crea una nueva instancia de {@code GenericPhoneMaskStrategy}. */
    public GenericPhoneMaskStrategy() {}

    private static final int VISIBLE_END_DIGITS = 3;

    @Override
    public MaskResult mask(String value, MaskConfig config) {
        char maskChar = config.maskChar();

        // Contar dígitos totales
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
                // Preservar separadores (Espacios, guiones, paréntesis)
                masked.append(ch);
            }
        }

        return new MaskResult(
                value,
                masked.toString(),
                MaskType.PHONE,
                CountryCode.GENERIC,
                true
        );
    }
}
