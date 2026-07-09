package pe.edu.nova.java.libs.mask.utils.strategy.phone;

import pe.edu.nova.java.libs.mask.utils.CountryCode;
import pe.edu.nova.java.libs.mask.utils.MaskType;
import pe.edu.nova.java.libs.mask.utils.config.MaskConfig;
import pe.edu.nova.java.libs.mask.utils.result.MaskResult;
import pe.edu.nova.java.libs.mask.utils.strategy.MaskStrategy;

/**
 * Estrategia de enmascaramiento para teléfonos peruanos.
 * <p>
 * Formato: +51 XXX XXX XXX
 * Preserva el código de país "+51", enmascara los dígitos centrales,
 * muestra los últimos 3 dígitos y preserva separadores en sus posiciones originales.
 * Ejemplo: "+51 987 654 321" → "+51 *** *** 321"
 */
public class PeruPhoneMaskStrategy implements MaskStrategy {

    /** Crea una nueva instancia de {@code PeruPhoneMaskStrategy}. */
    public PeruPhoneMaskStrategy() {}

    private static final String COUNTRY_CODE = "+51";
    private static final int VISIBLE_END_DIGITS = 3;

    @Override
    public MaskResult mask(String value, MaskConfig config) {
        char maskChar = config.maskChar();

        // Encontrar dónde termina el código de país
        String remaining = value;
        StringBuilder prefix = new StringBuilder();

        if (value.startsWith(COUNTRY_CODE)) {
            prefix.append(COUNTRY_CODE);
            remaining = value.substring(COUNTRY_CODE.length());
        }

        // Contar dígitos en la parte restante
        int totalDigits = 0;
        for (int i = 0; i < remaining.length(); i++) {
            if (Character.isDigit(remaining.charAt(i))) {
                totalDigits++;
            }
        }

        int digitsToMask = totalDigits - VISIBLE_END_DIGITS;
        StringBuilder maskedRemaining = new StringBuilder(remaining.length());
        int digitIndex = 0;

        for (int i = 0; i < remaining.length(); i++) {
            char ch = remaining.charAt(i);
            if (Character.isDigit(ch)) {
                if (digitIndex < digitsToMask) {
                    maskedRemaining.append(maskChar);
                } else {
                    maskedRemaining.append(ch);
                }
                digitIndex++;
            } else {
                // Preservar separadores (espacios, guiones, paréntesis)
                maskedRemaining.append(ch);
            }
        }

        String maskedValue = prefix.toString() + maskedRemaining;

        return new MaskResult(
                value,
                maskedValue,
                MaskType.PHONE,
                CountryCode.PE,
                true
        );
    }
}
