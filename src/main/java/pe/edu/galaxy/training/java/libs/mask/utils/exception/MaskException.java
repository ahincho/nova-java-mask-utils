package pe.edu.galaxy.training.java.libs.mask.utils.exception;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.MaskType;

/**
 * Excepción base para errores de enmascaramiento.
 * Extiende RuntimeException (unchecked) siguiendo prácticas modernas de Java.
 * Incluye contexto del tipo de enmascaramiento y país para diagnóstico.
 */
public class MaskException extends RuntimeException {

    /** Tipo de enmascaramiento asociado al error. */
    private final MaskType maskType;
    /** Código de país asociado al error. */
    private final CountryCode countryCode;

    /**
     * Crea una nueva MaskException con mensaje, tipo y país.
     *
     * @param message Descripción del error
     * @param maskType Tipo de enmascaramiento que causó el error
     * @param countryCode País asociado al error
     */
    public MaskException(String message, MaskType maskType, CountryCode countryCode) {
        super(message);
        this.maskType = maskType;
        this.countryCode = countryCode;
    }

    /**
     * Crea una nueva MaskException con mensaje, tipo, país y causa.
     *
     * @param message Descripción del error
     * @param maskType Tipo de enmascaramiento que causó el error
     * @param countryCode País asociado al error
     * @param cause Excepción original que causó este error
     */
    public MaskException(String message, MaskType maskType, CountryCode countryCode, Throwable cause) {
        super(message, cause);
        this.maskType = maskType;
        this.countryCode = countryCode;
    }

    /**
     * Retorna el tipo de enmascaramiento asociado al error.
     *
     * @return Tipo de enmascaramiento
     */
    public MaskType getMaskType() {
        return maskType;
    }

    /**
     * Retorna el código de país asociado al error.
     *
     * @return Código de país
     */
    public CountryCode getCountryCode() {
        return countryCode;
    }
}
