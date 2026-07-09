package pe.edu.galaxy.training.java.libs.mask.utils.strategy.bank;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.MaskType;
import pe.edu.galaxy.training.java.libs.mask.utils.config.MaskConfig;
import pe.edu.galaxy.training.java.libs.mask.utils.result.MaskResult;
import pe.edu.galaxy.training.java.libs.mask.utils.strategy.MaskStrategy;

/**
 * Estrategia de enmascaramiento genérica para cuentas bancarias.
 * <p>
 * Muestra los primeros 3 y los últimos 4 caracteres, enmascara el resto.
 * Fallback para formatos desconocidos.
 */
public class GenericBankAccountMaskStrategy implements MaskStrategy {

    /** Crea una nueva instancia de {@code GenericBankAccountMaskStrategy}. */
    public GenericBankAccountMaskStrategy() {}

    private static final int VISIBLE_START = 3;
    private static final int VISIBLE_END = 4;

    @Override
    public MaskResult mask(String value, MaskConfig config) {
        char maskChar = config.maskChar();
        int length = value.length();
        StringBuilder masked = new StringBuilder(length);
        int maskEnd = length - VISIBLE_END;
        for (int i = 0; i < length; i++) {
            if (i < VISIBLE_START || i >= maskEnd) {
                masked.append(value.charAt(i));
            } else {
                masked.append(maskChar);
            }
        }
        return new MaskResult(
            value,
            masked.toString(),
            MaskType.BANK_ACCOUNT,
            CountryCode.GENERIC,
            true
        );
    }
}
