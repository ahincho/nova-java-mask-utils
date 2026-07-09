package pe.edu.galaxy.training.java.libs.mask.utils.strategy.identity;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.MaskType;
import pe.edu.galaxy.training.java.libs.mask.utils.config.MaskConfig;
import pe.edu.galaxy.training.java.libs.mask.utils.result.MaskResult;
import pe.edu.galaxy.training.java.libs.mask.utils.strategy.MaskStrategy;

/**
 * Estrategia de enmascaramiento para DNI peruano (8 dígitos).
 * <p>
 * Enmascara los primeros 5 dígitos y muestra los últimos 3.
 * Ejemplo: "12345678" → "*****678"
 */
public class PeruIdentityMaskStrategy implements MaskStrategy {

    /** Crea una nueva instancia de {@code PeruIdentityMaskStrategy}. */
    public PeruIdentityMaskStrategy() {}

    private static final int VISIBLE_END = 3;

    @Override
    public MaskResult mask(String value, MaskConfig config) {
        char maskChar = config.maskChar();
        int length = value.length();

        StringBuilder masked = new StringBuilder(length);
        int maskCount = length - VISIBLE_END;

        for (int i = 0; i < length; i++) {
            if (i < maskCount) {
                masked.append(maskChar);
            } else {
                masked.append(value.charAt(i));
            }
        }

        return new MaskResult(
                value,
                masked.toString(),
                MaskType.IDENTITY_DOCUMENT,
                CountryCode.PE,
                true
        );
    }
}
