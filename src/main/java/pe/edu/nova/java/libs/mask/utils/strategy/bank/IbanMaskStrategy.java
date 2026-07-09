package pe.edu.nova.java.libs.mask.utils.strategy.bank;

import pe.edu.nova.java.libs.mask.utils.CountryCode;
import pe.edu.nova.java.libs.mask.utils.MaskType;
import pe.edu.nova.java.libs.mask.utils.config.MaskConfig;
import pe.edu.nova.java.libs.mask.utils.result.MaskResult;
import pe.edu.nova.java.libs.mask.utils.strategy.MaskStrategy;

/**
 * Estrategia de enmascaramiento para cuentas IBAN.
 * <p>
 * Muestra los primeros 4 caracteres (código de país + dígitos de control)
 * y los últimos 4, enmascara el resto. Preserva separadores en sus posiciones originales.
 * Ejemplo: "ES12 3456 7890 1234 5678" → "ES12 **** **** **** 5678"
 */
public class IbanMaskStrategy implements MaskStrategy {

    /** Crea una nueva instancia de {@code IbanMaskStrategy}. */
    public IbanMaskStrategy() {}

    private static final int VISIBLE_START_CHARS = 4;
    private static final int VISIBLE_END_CHARS = 4;

    @Override
    public MaskResult mask(String value, MaskConfig config) {
        char maskChar = config.maskChar();

        // Contar caracteres alfanuméricos totales (letras y dígitos)
        int totalAlphanumeric = 0;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (Character.isLetterOrDigit(ch)) {
                totalAlphanumeric++;
            }
        }

        int maskEnd = totalAlphanumeric - VISIBLE_END_CHARS;

        StringBuilder masked = new StringBuilder(value.length());
        int alphanumericIndex = 0;

        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (Character.isLetterOrDigit(ch)) {
                if (alphanumericIndex < VISIBLE_START_CHARS || alphanumericIndex >= maskEnd) {
                    masked.append(ch);
                } else {
                    masked.append(maskChar);
                }
                alphanumericIndex++;
            } else {
                // Preservar separadores (espacios, guiones)
                masked.append(ch);
            }
        }

        return new MaskResult(
                value,
                masked.toString(),
                MaskType.BANK_ACCOUNT,
                config.country(),
                true
        );
    }
}
