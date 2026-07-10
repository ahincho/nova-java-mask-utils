package pe.edu.nova.java.libs.mask.utils.strategy.name;

import pe.edu.nova.java.libs.mask.utils.MaskType;
import pe.edu.nova.java.libs.mask.utils.config.MaskConfig;
import pe.edu.nova.java.libs.mask.utils.result.MaskResult;
import pe.edu.nova.java.libs.mask.utils.strategy.MaskStrategy;

/**
 * Estrategia de enmascaramiento para nombres de personas.
 * <p>
 * Muestra la primera letra de cada palabra, enmascara el resto y preserva espacios.
 * <ul>
 *   <li>"Juan Perez" → "J*** P****"</li>
 *   <li>"Juan" → "J***"</li>
 *   <li>"" o solo espacios → ""</li>
 * </ul>
 * Independiente del país.
 */
public class PersonNameMaskStrategy implements MaskStrategy {

    /** Crea una nueva instancia de {@code PersonNameMaskStrategy}. */
    public PersonNameMaskStrategy() {}

    @Override
    public MaskResult mask(String value, MaskConfig config) {
        char maskChar = config.maskChar();

        // Vacío o solo espacios: Retornar cadena vacía
        if (value.isBlank()) {
            return new MaskResult(
                    value,
                    "",
                    MaskType.PERSON_NAME,
                    config.country(),
                    true
            );
        }

        StringBuilder masked = new StringBuilder(value.length());
        boolean isStartOfWord = true;

        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (ch == ' ') {
                masked.append(' ');
                isStartOfWord = true;
            } else if (isStartOfWord) {
                masked.append(ch);
                isStartOfWord = false;
            } else {
                masked.append(maskChar);
            }
        }

        return new MaskResult(
                value,
                masked.toString(),
                MaskType.PERSON_NAME,
                config.country(),
                true
        );
    }
}
