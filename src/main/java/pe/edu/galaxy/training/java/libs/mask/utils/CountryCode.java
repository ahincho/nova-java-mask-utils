package pe.edu.galaxy.training.java.libs.mask.utils;

import java.util.Locale;

/**
 * Código de país ISO 3166-1 alpha-2 para resolución de estrategias de enmascaramiento.
 */
public enum CountryCode {

    /** Perú. */
    PE("Perú"),
    /** Estados Unidos. */
    US("Estados Unidos"),
    /** Genérico (fallback para países no registrados). */
    GENERIC("Genérico");

    private final String displayName;

    CountryCode(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Retorna el nombre descriptivo del país.
     *
     * @return nombre del país
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Deriva el CountryCode desde el Locale del sistema.
     * Retorna GENERIC si el país del locale no tiene un CountryCode correspondiente.
     *
     * @return CountryCode derivado del locale del sistema
     */
    public static CountryCode fromLocale() {
        return fromCode(Locale.getDefault().getCountry());
    }

    /**
     * Convierte un código ISO 3166-1 alpha-2 a CountryCode.
     * La comparación es case-insensitive.
     * Retorna GENERIC si el código no tiene un CountryCode correspondiente.
     *
     * @param isoCode código ISO 3166-1 alpha-2 (por ejemplo, "PE", "US")
     * @return CountryCode correspondiente, o GENERIC si no hay coincidencia
     */
    public static CountryCode fromCode(String isoCode) {
        if (isoCode == null || isoCode.isBlank()) {
            return GENERIC;
        }
        String upper = isoCode.trim().toUpperCase(Locale.ROOT);
        for (CountryCode code : values()) {
            if (code != GENERIC && code.name().equals(upper)) {
                return code;
            }
        }
        return GENERIC;
    }
}
