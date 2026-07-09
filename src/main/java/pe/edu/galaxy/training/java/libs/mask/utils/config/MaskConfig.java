package pe.edu.galaxy.training.java.libs.mask.utils.config;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.MaskType;

/**
 * Configuración inmutable para operaciones de enmascaramiento.
 * <p>
 * Usa el patrón Builder para construcción fluida con validación.
 * Los valores predeterminados son: maskChar='*', visibleStart=0, visibleEnd=0,
 * country derivado del Locale del sistema.
 */
public final class MaskConfig {

    private final char maskChar;
    private final int visibleStart;
    private final int visibleEnd;
    private final CountryCode country;

    private MaskConfig(char maskChar, int visibleStart, int visibleEnd, CountryCode country) {
        this.maskChar = maskChar;
        this.visibleStart = visibleStart;
        this.visibleEnd = visibleEnd;
        this.country = country;
    }

    /**
     * Carácter utilizado para enmascarar (default: '*').
     *
     * @return Carácter de máscara
     */
    public char maskChar() {
        return maskChar;
    }

    /**
     * Cantidad de caracteres visibles al inicio del valor (default: 0).
     *
     * @return Cantidad de caracteres visibles al inicio
     */
    public int visibleStart() {
        return visibleStart;
    }

    /**
     * Cantidad de caracteres visibles al final del valor (default: 0).
     *
     * @return Cantidad de caracteres visibles al final
     */
    public int visibleEnd() {
        return visibleEnd;
    }

    /**
     * País para resolución de estrategia.
     *
     * @return Código de país
     */
    public CountryCode country() {
        return country;
    }

    /**
     * Retorna una configuración con todos los valores predeterminados.
     *
     * @return MaskConfig con defaults
     */
    public static MaskConfig defaults() {
        return builder().build();
    }

    /**
     * Retorna una configuración con valores predeterminados específicos para el tipo dado.
     *
     * @param type Tipo de enmascaramiento
     * @return MaskConfig con defaults para el tipo
     */
    public static MaskConfig forType(MaskType type) {
        return switch (type) {
            case EMAIL -> builder().visibleStart(1).visibleEnd(0).build();
            case PHONE -> builder().visibleStart(0).visibleEnd(3).build();
            case IDENTITY_DOCUMENT -> builder().visibleStart(0).visibleEnd(3).build();
            case CREDIT_CARD -> builder().visibleStart(6).visibleEnd(4).build();
            case BANK_ACCOUNT -> builder().visibleStart(3).visibleEnd(4).build();
            case PERSON_NAME -> builder().visibleStart(1).visibleEnd(0).build();
            case IP_ADDRESS -> builder().visibleStart(1).visibleEnd(0).build();
        };
    }

    /**
     * Crea un nuevo Builder para construir MaskConfig.
     *
     * @return Nuevo Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder para construir instancias inmutables de MaskConfig.
     */
    public static final class Builder {

        private char maskChar = '*';
        private int visibleStart = 0;
        private int visibleEnd = 0;
        private CountryCode country = CountryCode.fromLocale();

        private Builder() {
        }

        /**
         * Configura el carácter de máscara.
         * Si se pasa '\0' (null char), se usará '*' por defecto.
         *
         * @param maskChar Carácter de máscara
         * @return Este builder
         */
        public Builder maskChar(char maskChar) {
            this.maskChar = maskChar;
            return this;
        }

        /**
         * Configura la cantidad de caracteres visibles al inicio.
         *
         * @param visibleStart Cantidad de caracteres visibles al inicio
         * @return Este builder
         * @throws IllegalArgumentException si visibleStart es negativo
         */
        public Builder visibleStart(int visibleStart) {
            if (visibleStart < 0) {
                throw new IllegalArgumentException("visibleStart no debe ser negativo, se recibió: " + visibleStart);
            }
            this.visibleStart = visibleStart;
            return this;
        }

        /**
         * Configura la cantidad de caracteres visibles al final.
         *
         * @param visibleEnd Cantidad de caracteres visibles al final
         * @return Este builder
         * @throws IllegalArgumentException si visibleEnd es negativo
         */
        public Builder visibleEnd(int visibleEnd) {
            if (visibleEnd < 0) {
                throw new IllegalArgumentException("visibleEnd no debe ser negativo, se recibió: " + visibleEnd);
            }
            this.visibleEnd = visibleEnd;
            return this;
        }

        /**
         * Configura el país para resolución de estrategia.
         *
         * @param country Código de país
         * @return Este builder
         */
        public Builder country(CountryCode country) {
            this.country = country;
            return this;
        }

        /**
         * Construye la instancia inmutable de MaskConfig.
         *
         * @return Nueva instancia de MaskConfig
         */
        public MaskConfig build() {
            char effectiveMaskChar = (this.maskChar == '\0') ? '*' : this.maskChar;
            return new MaskConfig(effectiveMaskChar, visibleStart, visibleEnd, country);
        }
    }
}
