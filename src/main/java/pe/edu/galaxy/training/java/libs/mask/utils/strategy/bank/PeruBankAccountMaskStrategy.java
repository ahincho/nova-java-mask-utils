package pe.edu.galaxy.training.java.libs.mask.utils.strategy.bank;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.MaskType;
import pe.edu.galaxy.training.java.libs.mask.utils.config.MaskConfig;
import pe.edu.galaxy.training.java.libs.mask.utils.result.MaskResult;
import pe.edu.galaxy.training.java.libs.mask.utils.strategy.MaskStrategy;

/**
 * Estrategia de enmascaramiento para CCI peruano (20 dígitos).
 * <p>
 * Muestra los primeros 3 dígitos (código de banco) y los últimos 4,
 * enmascara el resto. Preserva separadores en sus posiciones originales.
 * Ejemplo: "00212345678901234567" → "002*************4567"
 */
public class PeruBankAccountMaskStrategy implements MaskStrategy {

    /** Crea una nueva instancia de {@code PeruBankAccountMaskStrategy}. */
    public PeruBankAccountMaskStrategy() {}

    private static final int VISIBLE_START_DIGITS = 3;
    private static final int VISIBLE_END_DIGITS = 4;

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

        int maskEnd = totalDigits - VISIBLE_END_DIGITS;

        StringBuilder masked = new StringBuilder(value.length());
        int digitIndex = 0;

        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (Character.isDigit(ch)) {
                if (digitIndex < VISIBLE_START_DIGITS || digitIndex >= maskEnd) {
                    masked.append(ch);
                } else {
                    masked.append(maskChar);
                }
                digitIndex++;
            } else {
                // Preservar separadores
                masked.append(ch);
            }
        }

        return new MaskResult(
                value,
                masked.toString(),
                MaskType.BANK_ACCOUNT,
                CountryCode.PE,
                true
        );
    }
}
