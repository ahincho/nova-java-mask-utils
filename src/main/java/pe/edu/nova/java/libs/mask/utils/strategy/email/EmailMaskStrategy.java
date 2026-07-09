package pe.edu.nova.java.libs.mask.utils.strategy.email;

import pe.edu.nova.java.libs.mask.utils.CountryCode;
import pe.edu.nova.java.libs.mask.utils.MaskType;
import pe.edu.nova.java.libs.mask.utils.config.MaskConfig;
import pe.edu.nova.java.libs.mask.utils.exception.InvalidFormatException;
import pe.edu.nova.java.libs.mask.utils.result.MaskResult;
import pe.edu.nova.java.libs.mask.utils.strategy.MaskStrategy;

/**
 * Estrategia de enmascaramiento para direcciones de email.
 * <p>
 * Muestra el primer carácter del username, enmascara el resto del username
 * y preserva el dominio completo.
 * <ul>
 *   <li>"john.doe@gmail.com" → "j*******@gmail.com"</li>
 *   <li>"j@gmail.com" → "*@gmail.com" (username de 1 carácter: completamente enmascarado)</li>
 * </ul>
 * Lanza {@link InvalidFormatException} si no contiene '@'.
 * Independiente del país (mismo resultado para cualquier CountryCode).
 */
public class EmailMaskStrategy implements MaskStrategy {

    /** Crea una nueva instancia de {@code EmailMaskStrategy}. */
    public EmailMaskStrategy() {}

    @Override
    public MaskResult mask(String value, MaskConfig config) {
        char maskChar = config.maskChar();

        int atIndex = value.indexOf('@');
        if (atIndex < 0) {
            throw new InvalidFormatException(
                    "Formato de email inválido: falta '@' en el valor",
                    MaskType.EMAIL,
                    config.country()
            );
        }

        String username = value.substring(0, atIndex);
        String domain = value.substring(atIndex); // Incluye '@'

        StringBuilder maskedUsername = new StringBuilder(username.length());

        if (username.length() <= 1) {
            // Username de 1 carácter: Completamente enmascarado
            for (int i = 0; i < username.length(); i++) {
                maskedUsername.append(maskChar);
            }
        } else {
            // Mostrar primer carácter, enmascarar el resto
            maskedUsername.append(username.charAt(0));
            for (int i = 1; i < username.length(); i++) {
                maskedUsername.append(maskChar);
            }
        }

        String maskedValue = maskedUsername.toString() + domain;

        return new MaskResult(
                value,
                maskedValue,
                MaskType.EMAIL,
                config.country(),
                true
        );
    }
}
