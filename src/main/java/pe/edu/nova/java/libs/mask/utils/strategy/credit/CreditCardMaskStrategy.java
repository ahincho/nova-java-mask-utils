package pe.edu.nova.java.libs.mask.utils.strategy.credit;

import pe.edu.nova.java.libs.mask.utils.MaskType;
import pe.edu.nova.java.libs.mask.utils.config.MaskConfig;
import pe.edu.nova.java.libs.mask.utils.exception.InvalidFormatException;
import pe.edu.nova.java.libs.mask.utils.result.MaskResult;
import pe.edu.nova.java.libs.mask.utils.strategy.MaskStrategy;

/**
 * Estrategia de enmascaramiento para tarjetas de crédito (PCI DSS).
 * <p>
 * Muestra los primeros 6 dígitos (BIN) y los últimos 4, enmascara los intermedios.
 * Preserva separadores (espacios, guiones) en sus posiciones originales.
 * <ul>
 *   <li>"4532 0123 4567 8901" → "4532 01** **** 8901"</li>
 * </ul>
 * Lanza {@link InvalidFormatException} si hay menos de 13 dígitos.
 * Independiente del país.
 */
public class CreditCardMaskStrategy implements MaskStrategy {

    /** Crea una nueva instancia de {@code CreditCardMaskStrategy}. */
    public CreditCardMaskStrategy() {}

    private static final int VISIBLE_START_DIGITS = 6;
    private static final int VISIBLE_END_DIGITS = 4;
    private static final int MIN_DIGITS = 13;

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

        if (totalDigits < MIN_DIGITS) {
            throw new InvalidFormatException(
                    String.format("Formato de tarjeta de crédito inválido: se esperaban al menos %d dígitos, se recibieron %d", MIN_DIGITS, totalDigits),
                    MaskType.CREDIT_CARD,
                    config.country()
            );
        }

        int maskStart = VISIBLE_START_DIGITS;
        int maskEnd = totalDigits - VISIBLE_END_DIGITS;

        StringBuilder masked = new StringBuilder(value.length());
        int digitIndex = 0;

        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (Character.isDigit(ch)) {
                if (digitIndex >= maskStart && digitIndex < maskEnd) {
                    masked.append(maskChar);
                } else {
                    masked.append(ch);
                }
                digitIndex++;
            } else {
                // Preservar separadores (espacios, guiones)
                masked.append(ch);
            }
        }

        return new MaskResult(
                value,
                masked.toString(),
                MaskType.CREDIT_CARD,
                config.country(),
                true
        );
    }
}
