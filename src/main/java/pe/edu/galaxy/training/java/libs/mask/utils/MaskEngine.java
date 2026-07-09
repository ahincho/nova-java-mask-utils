package pe.edu.galaxy.training.java.libs.mask.utils;

import java.util.Map;

import pe.edu.galaxy.training.java.libs.mask.utils.annotation.AnnotationProcessor;
import pe.edu.galaxy.training.java.libs.mask.utils.config.MaskConfig;
import pe.edu.galaxy.training.java.libs.mask.utils.log.LogMasker;
import pe.edu.galaxy.training.java.libs.mask.utils.result.MaskResult;
import pe.edu.galaxy.training.java.libs.mask.utils.strategy.MaskStrategy;
import pe.edu.galaxy.training.java.libs.mask.utils.strategy.StrategyRegistry;

/**
 * Punto de entrada principal de la librería mask-utils.
 * <p>
 * Clase final con constructor privado (patrón utility class).
 * Provee métodos estáticos para uso directo y un Builder interno para API fluida.
 */
public final class MaskEngine {

    private MaskEngine() {}

    /**
     * Enmascara un valor con tipo específico, usando país del sistema.
     * 
     * @param value valor a enmascarar
     * @param type tipo de enmascaramiento
     * @return resultado del enmascaramiento
     */
    public static MaskResult mask(String value, MaskType type) {
        return mask(value, type, CountryCode.fromLocale());
    }

    /**
     * Enmascara un valor con tipo y país específicos, usando configuración por defecto.
     * 
     * @param value valor a enmascarar
     * @param type tipo de enmascaramiento
     * @param country código de país
     * @return resultado del enmascaramiento
     */
    public static MaskResult mask(String value, MaskType type, CountryCode country) {
        return mask(value, type, country, MaskConfig.defaults());
    }

    /**
     * Enmascara un valor con tipo, país y configuración personalizada.
     *
     * @param value valor a enmascarar
     * @param type tipo de enmascaramiento
     * @param country código de país
     * @param config configuración de enmascaramiento
     * @return resultado del enmascaramiento
     */
    public static MaskResult mask(String value, MaskType type, CountryCode country, MaskConfig config) {
        // Manejo de nulos
        if (value == null) {
            return new MaskResult(null, null, type, country, false);
        }
        // Manejo de vacíos
        if (value.isEmpty()) {
            return new MaskResult("", "", type, country, false);
        }
        // Resolver estrategia y delegar
        MaskStrategy strategy = StrategyRegistry.getDefault().resolve(type, country);
        return strategy.mask(value, config);
    }

    /**
     * Enmascara datos sensibles detectados automáticamente en texto.
     * Delega a {@link LogMasker#maskAutoDetect} con país del sistema y registro por defecto.
     *
     * @param text texto a procesar
     * @return texto con datos sensibles enmascarados
     */
    public static String maskLog(String text) {
        return LogMasker.maskAutoDetect(text, CountryCode.fromLocale(), StrategyRegistry.getDefault());
    }

    /**
     * Enmascara datos sensibles en texto con patrones explícitos.
     * Delega a {@link LogMasker#maskPatterns} con país del sistema y registro por defecto.
     *
     * @param text texto a procesar
     * @param patterns mapa de patrones regex a tipos de enmascaramiento
     * @return texto con datos sensibles enmascarados
     */
    public static String maskLog(String text, Map<String, MaskType> patterns) {
        return LogMasker.maskPatterns(text, patterns, CountryCode.fromLocale(), StrategyRegistry.getDefault());
    }

    /**
     * Procesa un objeto y enmascara campos anotados con @Masked.
     * Delega a {@link AnnotationProcessor#process} con el registro por defecto.
     *
     * @param object objeto a procesar
     * @param <T> tipo del objeto
     * @return objeto con campos enmascarados
     */
    public static <T> T maskAnnotated(T object) {
        return AnnotationProcessor.process(object, StrategyRegistry.getDefault());
    }

    /**
     * Crea un nuevo Builder para construir operaciones de enmascaramiento de forma fluida.
     *
     * @return nuevo Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder para API fluida de enmascaramiento.
     * Permite configurar tipo, país, configuración y parámetros individuales
     * antes de ejecutar el enmascaramiento.
     */
    public static final class Builder {

        private MaskType type;
        private CountryCode country;
        private MaskConfig config;
        private Character maskChar;
        private Integer visibleStart;
        private Integer visibleEnd;

        private Builder() {}

        /**
         * Configura el tipo de enmascaramiento.
         *
         * @param type tipo de enmascaramiento
         * @return este builder
         */
        public Builder type(MaskType type) {
            this.type = type;
            return this;
        }

        /**
         * Configura el código de país.
         *
         * @param country código de país
         * @return este builder
         */
        public Builder country(CountryCode country) {
            this.country = country;
            return this;
        }

        /**
         * Configura la configuración de enmascaramiento completa.
         * Si se establece, los campos individuales (maskChar, visibleStart, visibleEnd) se ignoran.
         *
         * @param config configuración de enmascaramiento
         * @return este builder
         */
        public Builder config(MaskConfig config) {
            this.config = config;
            return this;
        }

        /**
         * Configura el carácter de máscara.
         *
         * @param maskChar carácter de máscara
         * @return este builder
         */
        public Builder maskChar(char maskChar) {
            this.maskChar = maskChar;
            return this;
        }

        /**
         * Configura la cantidad de caracteres visibles al inicio.
         *
         * @param count cantidad de caracteres visibles al inicio
         * @return este builder
         */
        public Builder visibleStart(int count) {
            this.visibleStart = count;
            return this;
        }

        /**
         * Configura la cantidad de caracteres visibles al final.
         *
         * @param count cantidad de caracteres visibles al final
         * @return este builder
         */
        public Builder visibleEnd(int count) {
            this.visibleEnd = count;
            return this;
        }

        /**
         * Ejecuta el enmascaramiento con la configuración acumulada.
         *
         * @param value valor a enmascarar
         * @return resultado del enmascaramiento
         */
        public MaskResult mask(String value) {
            CountryCode effectiveCountry = (this.country != null) ? this.country : CountryCode.fromLocale();
            MaskConfig effectiveConfig;
            if (this.config != null) {
                // Si se proporcionó config directamente, usarla
                effectiveConfig = this.config;
            } else {
                // Construir config desde campos individuales
                MaskConfig.Builder configBuilder = MaskConfig.builder()
                        .country(effectiveCountry);
                if (this.maskChar != null) {
                    configBuilder.maskChar(this.maskChar);
                }
                if (this.visibleStart != null) {
                    configBuilder.visibleStart(this.visibleStart);
                }
                if (this.visibleEnd != null) {
                    configBuilder.visibleEnd(this.visibleEnd);
                }
                effectiveConfig = configBuilder.build();
            }
            return MaskEngine.mask(value, this.type, effectiveCountry, effectiveConfig);
        }
    }
}
