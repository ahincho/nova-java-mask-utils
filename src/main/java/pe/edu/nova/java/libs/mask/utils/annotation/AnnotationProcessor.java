package pe.edu.nova.java.libs.mask.utils.annotation;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

import pe.edu.nova.java.libs.mask.utils.CountryCode;
import pe.edu.nova.java.libs.mask.utils.MaskType;
import pe.edu.nova.java.libs.mask.utils.config.MaskConfig;
import pe.edu.nova.java.libs.mask.utils.result.MaskResult;
import pe.edu.nova.java.libs.mask.utils.strategy.MaskStrategy;
import pe.edu.nova.java.libs.mask.utils.strategy.StrategyRegistry;

/**
 * Procesa objetos por reflexión, enmascarando campos anotados con {@link Masked}.
 * <p>
 * Para cada campo {@code @Masked}:
 * <ol>
 *   <li>Obtiene el valor del campo (haciéndolo accesible si es privado)</li>
 *   <li>Convierte a String si no es String (usando {@code String.valueOf()})</li>
 *   <li>Determina el {@link MaskType}: usa el tipo de la anotación, o infiere del nombre del campo</li>
 *   <li>Determina el {@link CountryCode}: usa el de la anotación, o el de {@link MaskConfigAnnotation}, o el del sistema</li>
 *   <li>Enmascara el valor usando el registro de estrategias</li>
 *   <li>Establece el valor enmascarado de vuelta en el campo (como String)</li>
 * </ol>
 * Los campos sin {@code @Masked} se preservan sin cambios.
 */
public final class AnnotationProcessor {

    private AnnotationProcessor() {}

    /**
     * Mapa de nombres de campo (en minúsculas) a tipos de enmascaramiento para inferencia.
     */
    private static final Map<String, MaskType> FIELD_NAME_TO_TYPE = Map.ofEntries(
            Map.entry("email", MaskType.EMAIL),
            Map.entry("correo", MaskType.EMAIL),
            Map.entry("mail", MaskType.EMAIL),
            Map.entry("phone", MaskType.PHONE),
            Map.entry("telefono", MaskType.PHONE),
            Map.entry("tel", MaskType.PHONE),
            Map.entry("celular", MaskType.PHONE),
            Map.entry("dni", MaskType.IDENTITY_DOCUMENT),
            Map.entry("ssn", MaskType.IDENTITY_DOCUMENT),
            Map.entry("document", MaskType.IDENTITY_DOCUMENT),
            Map.entry("documento", MaskType.IDENTITY_DOCUMENT),
            Map.entry("passport", MaskType.IDENTITY_DOCUMENT),
            Map.entry("pasaporte", MaskType.IDENTITY_DOCUMENT),
            Map.entry("creditcard", MaskType.CREDIT_CARD),
            Map.entry("tarjeta", MaskType.CREDIT_CARD),
            Map.entry("card", MaskType.CREDIT_CARD),
            Map.entry("account", MaskType.BANK_ACCOUNT),
            Map.entry("cuenta", MaskType.BANK_ACCOUNT),
            Map.entry("iban", MaskType.BANK_ACCOUNT),
            Map.entry("cci", MaskType.BANK_ACCOUNT),
            Map.entry("name", MaskType.PERSON_NAME),
            Map.entry("nombre", MaskType.PERSON_NAME),
            Map.entry("firstname", MaskType.PERSON_NAME),
            Map.entry("lastname", MaskType.PERSON_NAME),
            Map.entry("ip", MaskType.IP_ADDRESS),
            Map.entry("ipaddress", MaskType.IP_ADDRESS)
    );

    /**
     * Procesa un objeto, enmascarando campos anotados con {@link Masked}.
     * <p>
     * Modifica el objeto in-place y lo retorna. Los campos sin {@code @Masked}
     * se preservan sin cambios.
     * </p>
     * @param object Objeto a procesar
     * @param registry Registro de estrategias de enmascaramiento
     * @param <T> Tipo del objeto
     * @return El mismo objeto con campos enmascarados
     */
    public static <T> T process(T object, StrategyRegistry registry) {
        if (object == null) {
            return null;
        }
        Class<?> clazz = object.getClass();
        // Leer defaults de @MaskConfigAnnotation a nivel de clase
        char classMaskChar = '*';
        String classCountry = "";
        MaskConfigAnnotation classConfig = clazz.getAnnotation(MaskConfigAnnotation.class);
        if (classConfig != null) {
            classMaskChar = classConfig.maskChar();
            classCountry = classConfig.country();
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Masked masked = field.getAnnotation(Masked.class);
            if (masked == null) {
                continue;
            }
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(object);
                if (fieldValue == null) {
                    continue;
                }
                // Convertir a String
                String stringValue;
                if (fieldValue instanceof String s) {
                    stringValue = s;
                } else {
                    stringValue = String.valueOf(fieldValue);
                }
                if (stringValue.isEmpty()) {
                    continue;
                }
                // Determinar MaskType
                MaskType maskType = resolveMaskType(masked, field.getName());
                // Determinar CountryCode
                CountryCode country = resolveCountry(masked.country(), classCountry);
                // Construir MaskConfig
                MaskConfig config = MaskConfig.builder()
                        .maskChar(classMaskChar)
                        .country(country)
                        .build();
                // Resolver estrategia y enmascarar
                MaskStrategy strategy = registry.resolve(maskType, country);
                MaskResult result = strategy.mask(stringValue, config);
                // Establecer el valor enmascarado de vuelta (como String)
                field.set(object, result.maskedValue());
            } catch (IllegalAccessException e) {
                // No debería ocurrir ya que llamamos setAccessible(true)
                throw new RuntimeException("No se pudo acceder al campo: " + field.getName(), e);
            }
        }
        return object;
    }

    /**
     * Resuelve el {@link MaskType} desde la anotación o lo infiere del nombre del campo.
     * <p>
     * Si la anotación especifica un tipo no predeterminado, lo usa.
     * De lo contrario, intenta inferir del nombre del campo.
     * Retorna el tipo de la anotación (EMAIL) si no hay coincidencia de inferencia.
     *
     * @param masked Anotación {@link Masked} del campo
     * @param fieldName Nombre del campo para inferencia
     * @return Tipo de enmascaramiento resuelto
     */
    private static MaskType resolveMaskType(Masked masked, String fieldName) {
        // La anotación siempre tiene un tipo (default es EMAIL).
        // Usamos inferencia por nombre de campo solo cuando el tipo es el predeterminado (EMAIL),
        // permitiendo que el nombre del campo sobreescriba el default.
        MaskType annotationType = masked.type();
        // Si se estableció explícitamente a algo diferente del default, usarlo
        if (annotationType != MaskType.EMAIL) {
            return annotationType;
        }
        // Intentar inferir del nombre del campo
        String lowerName = fieldName.toLowerCase(Locale.ROOT);
        MaskType inferred = FIELD_NAME_TO_TYPE.get(lowerName);
        if (inferred != null) {
            return inferred;
        }
        // Sin coincidencia de inferencia — usar el default de la anotación (EMAIL)
        return annotationType;
    }

    /**
     * Resuelve el {@link CountryCode} desde nivel de campo, nivel de clase, o default del sistema.
     * <p>
     * El país a nivel de campo tiene prioridad sobre el de clase,
     * y el de clase sobre el default del sistema.
     *
     * @param fieldCountry Código de país a nivel de campo (puede ser nulo o vacío)
     * @param classCountry Código de país a nivel de clase (puede ser nulo o vacío)
     * @return Código de país resuelto
     */
    private static CountryCode resolveCountry(String fieldCountry, String classCountry) {
        // El país a nivel de campo tiene prioridad
        if (fieldCountry != null && !fieldCountry.isEmpty()) {
            return CountryCode.fromCode(fieldCountry);
        }
        // País a nivel de clase
        if (classCountry != null && !classCountry.isEmpty()) {
            return CountryCode.fromCode(classCountry);
        }
        // Default del sistema
        return CountryCode.fromLocale();
    }
}
