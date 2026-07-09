package pe.edu.nova.java.libs.mask.utils.log;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pe.edu.nova.java.libs.mask.utils.CountryCode;
import pe.edu.nova.java.libs.mask.utils.MaskType;
import pe.edu.nova.java.libs.mask.utils.config.MaskConfig;
import pe.edu.nova.java.libs.mask.utils.result.MaskResult;
import pe.edu.nova.java.libs.mask.utils.strategy.MaskStrategy;
import pe.edu.nova.java.libs.mask.utils.strategy.StrategyRegistry;

/**
 * Enmascaramiento de patrones sensibles en texto plano (logs).
 * <p>
 * Provee dos modos de operación:
 * <ul>
 *   <li>{@link #maskAutoDetect}: Detecta automáticamente emails, tarjetas de crédito,
 *       direcciones IP y teléfonos con código de país en el texto.</li>
 *   <li>{@link #maskPatterns}: Enmascara patrones explícitos definidos por regex.</li>
 * </ul>
 * Preserva todo el texto circundante sin modificar.
 */
public final class LogMasker {

    private LogMasker() {}

    private static final String EMAIL_REGEX = "[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}";

    private static final String CREDIT_CARD_REGEX = "\\b\\d{4}[\\s\\-]?\\d{4}[\\s\\-]?\\d{4}[\\s\\-]?\\d{4}\\b";

    private static final String IPV4_REGEX = "\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b";

    private static final String PHONE_REGEX = "\\+\\d{1,3}[\\s\\-]?\\d{3}[\\s\\-]?\\d{3}[\\s\\-]?\\d{3,4}";

    /**
     * Mapa de patrones de auto-detección a su MaskType correspondiente.
     * El orden importa: los patrones más específicos deben ir primero para evitar
     * que coincidencias parciales sean consumidas por patrones más amplios.
     */
    private static final Map<Pattern, MaskType> AUTO_DETECT_PATTERNS;

    static {
        AUTO_DETECT_PATTERNS = new LinkedHashMap<>();
        AUTO_DETECT_PATTERNS.put(Pattern.compile(EMAIL_REGEX), MaskType.EMAIL);
        AUTO_DETECT_PATTERNS.put(Pattern.compile(CREDIT_CARD_REGEX), MaskType.CREDIT_CARD);
        AUTO_DETECT_PATTERNS.put(Pattern.compile(IPV4_REGEX), MaskType.IP_ADDRESS);
        AUTO_DETECT_PATTERNS.put(Pattern.compile(PHONE_REGEX), MaskType.PHONE);
    }

    /**
     * Detecta y enmascara automáticamente patrones conocidos en texto.
     * <p>
     * Patrones detectados: Emails, tarjetas de crédito (16 dígitos),
     * direcciones IPv4 y teléfonos con código de país.
     *
     * @param text Texto a procesar
     * @param country Código de país para resolución de estrategia
     * @param registry Registro de estrategias
     * @return Texto con datos sensibles enmascarados, o null si text es null
     */
    public static String maskAutoDetect(String text, CountryCode country, StrategyRegistry registry) {
        if (text == null) {
            return null;
        }
        String result = text;
        MaskConfig config = MaskConfig.defaults();
        for (Map.Entry<Pattern, MaskType> entry : AUTO_DETECT_PATTERNS.entrySet()) {
            result = maskWithPattern(result, entry.getKey(), entry.getValue(), country, registry, config);
        }
        return result;
    }

    /**
     * Enmascara patrones explícitos definidos por regex en el texto.
     *
     * @param text Texto a procesar
     * @param patterns Mapa de patrones regex (String) a tipos de enmascaramiento
     * @param country Código de país para resolución de estrategia
     * @param registry Registro de estrategias
     * @return Texto con datos sensibles enmascarados, o null si text es null
     */
    public static String maskPatterns(String text, Map<String, MaskType> patterns, CountryCode country, StrategyRegistry registry) {
        if (text == null) {
            return null;
        }
        String result = text;
        MaskConfig config = MaskConfig.defaults();
        for (Map.Entry<String, MaskType> entry : patterns.entrySet()) {
            Pattern pattern = Pattern.compile(entry.getKey());
            result = maskWithPattern(result, pattern, entry.getValue(), country, registry, config);
        }
        return result;
    }

    /**
     * Reemplaza todas las coincidencias del patrón dado en el texto con sus equivalentes enmascarados.
     *
     * @param text Texto donde buscar coincidencias
     * @param pattern Patrón regex a buscar
     * @param maskType Tipo de enmascaramiento a aplicar
     * @param country Código de país para resolución de estrategia
     * @param registry Registro de estrategias
     * @param config Configuración de enmascaramiento
     * @return Texto con las coincidencias enmascaradas
     */
    private static String maskWithPattern(String text, Pattern pattern, MaskType maskType, CountryCode country, StrategyRegistry registry, MaskConfig config) {
        MaskStrategy strategy;
        try {
            strategy = registry.resolve(maskType, country);
        } catch (Exception e) {
            // Si no se encuentra estrategia, omitir este patrón
            return text;
        }
        Matcher matcher = pattern.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String matched = matcher.group();
            String masked;
            try {
                MaskResult maskResult = strategy.mask(matched, config);
                masked = maskResult.maskedValue();
            } catch (Exception e) {
                // Si el enmascaramiento falla (ej., formato inválido), mantener texto original
                masked = matched;
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(masked));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
