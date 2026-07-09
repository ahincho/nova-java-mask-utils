package pe.edu.nova.java.libs.mask.utils.result;

import pe.edu.nova.java.libs.mask.utils.CountryCode;
import pe.edu.nova.java.libs.mask.utils.MaskType;

/**
 * Resultado inmutable de una operación de enmascaramiento.
 *
 * @param originalValue Valor original antes del enmascaramiento
 * @param maskedValue Valor después del enmascaramiento
 * @param maskType Tipo de enmascaramiento aplicado
 * @param countryCode País utilizado para la resolución de estrategia
 * @param wasMasked Indica si el valor fue efectivamente enmascarado
 */
public record MaskResult(
    String originalValue,
    String maskedValue,
    MaskType maskType,
    CountryCode countryCode,
    boolean wasMasked
) {

    /**
     * Conveniencia: Retorna el valor enmascarado como String.
     *
     * @return Valor enmascarado
     */
    public String value() {
        return maskedValue;
    }
}
