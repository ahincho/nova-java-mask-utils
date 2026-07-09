package pe.edu.galaxy.training.java.libs.mask.utils.exception;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.MaskType;

/**
 * Excepción lanzada cuando un valor no tiene el formato esperado
 * para el MaskType indicado (por ejemplo, un email sin '@', una tarjeta
 * con menos de 13 dígitos, o una IP inválida).
 */
public class InvalidFormatException extends MaskException {

    /**
     * Crea una nueva InvalidFormatException.
     *
     * @param message Descripción del error de formato
     * @param maskType Tipo de enmascaramiento que esperaba un formato específico
     * @param countryCode País asociado a la operación
     */
    public InvalidFormatException(String message, MaskType maskType, CountryCode countryCode) {
        super(message, maskType, countryCode);
    }

    /**
     * Crea una nueva InvalidFormatException con causa.
     *
     * @param message Descripción del error de formato
     * @param maskType Tipo de enmascaramiento que esperaba un formato específico
     * @param countryCode País asociado a la operación
     * @param cause Excepción original
     */
    public InvalidFormatException(String message, MaskType maskType, CountryCode countryCode, Throwable cause) {
        super(message, maskType, countryCode, cause);
    }
}
