package pe.edu.galaxy.training.java.libs.mask.utils.strategy.identity;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.MaskType;
import pe.edu.galaxy.training.java.libs.mask.utils.config.MaskConfig;
import pe.edu.galaxy.training.java.libs.mask.utils.result.MaskResult;
import pe.edu.galaxy.training.java.libs.mask.utils.strategy.MaskStrategy;

/**
 * Estrategia de enmascaramiento genérica para documentos de identidad (pasaporte, etc.).
 * <p>
 * Muestra los primeros 2 caracteres y enmascara el resto.
 * Si la longitud del valor es ≤ 2, retorna completamente enmascarado.
 * Ejemplo: "AB1234567" → "AB*******"
 */
public class GenericIdentityMaskStrategy implements MaskStrategy {

    /** Crea una nueva instancia de {@code GenericIdentityMaskStrategy}. */
    public GenericIdentityMaskStrategy() {}

    private static final int VISIBLE_START = 2;

    @Override
    public MaskResult mask(String value, MaskConfig config) {
        char maskChar = config.maskChar();
        int length = value.length();

        StringBuilder masked = new StringBuilder(length);

        if (length <= VISIBLE_START) {
            // Completamente enmascarado cuando el valor es muy corto
            for (int i = 0; i < length; i++) {
                masked.append(maskChar);
            }
        } else {
            for (int i = 0; i < length; i++) {
                if (i < VISIBLE_START) {
                    masked.append(value.charAt(i));
                } else {
                    masked.append(maskChar);
                }
            }
        }

        return new MaskResult(
                value,
                masked.toString(),
                MaskType.IDENTITY_DOCUMENT,
                CountryCode.GENERIC,
                true
        );
    }
}
