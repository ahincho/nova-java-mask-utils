package pe.edu.galaxy.training.java.libs.mask.utils.exception;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.MaskType;

/**
 * Excepción lanzada cuando un MaskType no tiene una estrategia registrada
 * en el StrategyRegistry para el CountryCode dado (ni como fallback GENERIC).
 */
public class UnsupportedMaskTypeException extends MaskException {

    /**
     * Crea una nueva UnsupportedMaskTypeException.
     *
     * @param message Descripción del error
     * @param maskType Tipo de enmascaramiento no soportado
     * @param countryCode País para el cual no se encontró estrategia
     */
    public UnsupportedMaskTypeException(String message, MaskType maskType, CountryCode countryCode) {
        super(message, maskType, countryCode);
    }

    /**
     * Crea una nueva UnsupportedMaskTypeException con causa.
     *
     * @param message Descripción del error
     * @param maskType Tipo de enmascaramiento no soportado
     * @param countryCode País para el cual no se encontró estrategia
     * @param cause Excepción original
     */
    public UnsupportedMaskTypeException(String message, MaskType maskType, CountryCode countryCode, Throwable cause) {
        super(message, maskType, countryCode, cause);
    }
}
