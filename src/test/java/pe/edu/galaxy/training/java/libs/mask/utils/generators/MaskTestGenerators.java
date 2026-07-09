package pe.edu.galaxy.training.java.libs.mask.utils.generators;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.MaskType;
import pe.edu.galaxy.training.java.libs.mask.utils.config.MaskConfig;

/**
 * Generadores jqwik compartidos para property-based testing de mask-utils.
 * <p>
 * Provee generadores reutilizables para emails, documentos de identidad,
 * teléfonos, tarjetas de crédito, cuentas bancarias, IPs, nombres,
 * configuraciones y strings de caos.
 */
public class MaskTestGenerators {

    private MaskTestGenerators() {}

    /**
     * Genera emails válidos con usernames de 1-50 caracteres y dominios válidos.
     *
     * @return generador de emails válidos
     */
    @Provide
    public static Arbitrary<String> validEmails() {
        Arbitrary<String> usernames = Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(1)
                .ofMaxLength(50);
        Arbitrary<String> domains = Arbitraries.of(
                "gmail.com", "hotmail.com", "yahoo.com", "outlook.com",
                "empresa.pe", "company.us", "test.org", "example.co.uk"
        );
        return Combinators.combine(usernames, domains)
                .as((user, domain) -> user + "@" + domain);
    }

    /**
     * Genera DNI peruanos de exactamente 8 dígitos.
     *
     * @return generador de DNI peruanos
     */
    @Provide
    public static Arbitrary<String> peruDni() {
        return Arbitraries.strings()
                .withCharRange('0', '9')
                .ofLength(8);
    }

    /**
     * Genera SSN estadounidenses con formato XXX-XX-XXXX.
     *
     * @return generador de SSN
     */
    @Provide
    public static Arbitrary<String> usSsn() {
        Arbitrary<String> part1 = Arbitraries.strings().withCharRange('0', '9').ofLength(3);
        Arbitrary<String> part2 = Arbitraries.strings().withCharRange('0', '9').ofLength(2);
        Arbitrary<String> part3 = Arbitraries.strings().withCharRange('0', '9').ofLength(4);
        return Combinators.combine(part1, part2, part3)
                .as((p1, p2, p3) -> p1 + "-" + p2 + "-" + p3);
    }

    /**
     * Genera teléfonos peruanos con formato +51 XXX XXX XXX.
     *
     * @return generador de teléfonos peruanos
     */
    @Provide
    public static Arbitrary<String> peruPhone() {
        Arbitrary<String> g1 = Arbitraries.strings().withCharRange('0', '9').ofLength(3);
        Arbitrary<String> g2 = Arbitraries.strings().withCharRange('0', '9').ofLength(3);
        Arbitrary<String> g3 = Arbitraries.strings().withCharRange('0', '9').ofLength(3);
        return Combinators.combine(g1, g2, g3)
                .as((a, b, c) -> "+51 " + a + " " + b + " " + c);
    }

    /**
     * Genera teléfonos estadounidenses con formato +1 XXX-XXX-XXXX.
     *
     * @return generador de teléfonos US
     */
    @Provide
    public static Arbitrary<String> usPhone() {
        Arbitrary<String> g1 = Arbitraries.strings().withCharRange('0', '9').ofLength(3);
        Arbitrary<String> g2 = Arbitraries.strings().withCharRange('0', '9').ofLength(3);
        Arbitrary<String> g3 = Arbitraries.strings().withCharRange('0', '9').ofLength(4);
        return Combinators.combine(g1, g2, g3)
                .as((a, b, c) -> "+1 " + a + "-" + b + "-" + c);
    }

    /**
     * Genera números de tarjeta de crédito de 13-19 dígitos con separadores opcionales.
     *
     * @return generador de tarjetas de crédito válidas
     */
    @Provide
    public static Arbitrary<String> validCreditCard() {
        return Arbitraries.integers().between(13, 19).flatMap(length ->
                Arbitraries.strings().withCharRange('0', '9').ofLength(length).map(digits -> {
                    if (digits.length() == 16) {
                        return digits.substring(0, 4) + " " + digits.substring(4, 8) + " "
                                + digits.substring(8, 12) + " " + digits.substring(12, 16);
                    }
                    return digits;
                })
        );
    }

    /**
     * Genera CCI peruanos de exactamente 20 dígitos.
     *
     * @return generador de CCI peruanos
     */
    @Provide
    public static Arbitrary<String> peruCci() {
        return Arbitraries.strings()
                .withCharRange('0', '9')
                .ofLength(20);
    }

    /**
     * Genera IBAN válidos con formato CCDD + dígitos (mínimo 15 caracteres alfanuméricos).
     *
     * @return generador de IBAN válidos
     */
    @Provide
    public static Arbitrary<String> validIban() {
        Arbitrary<String> countryCode = Arbitraries.of("ES", "DE", "FR", "GB", "IT", "NL");
        Arbitrary<String> checkDigits = Arbitraries.strings().withCharRange('0', '9').ofLength(2);
        Arbitrary<String> bban = Arbitraries.strings().withCharRange('0', '9')
                .ofMinLength(10).ofMaxLength(26);
        return Combinators.combine(countryCode, checkDigits, bban)
                .as((cc, cd, b) -> cc + cd + b);
    }

    /**
     * Genera direcciones IPv4 válidas con octetos entre 0-255.
     *
     * @return generador de IPv4 válidas
     */
    @Provide
    public static Arbitrary<String> validIpv4() {
        Arbitrary<Integer> octet = Arbitraries.integers().between(0, 255);
        return Combinators.combine(octet, octet, octet, octet)
                .as((a, b, c, d) -> a + "." + b + "." + c + "." + d);
    }

    /**
     * Genera direcciones IPv6 válidas con 8 grupos de 4 dígitos hexadecimales.
     *
     * @return generador de IPv6 válidas
     */
    @Provide
    public static Arbitrary<String> validIpv6() {
        Arbitrary<String> group = Arbitraries.strings()
                .withCharRange('0', '9')
                .withCharRange('a', 'f')
                .ofLength(4);
        return Combinators.combine(group, group, group, group, group, group, group, group)
                .as((g1, g2, g3, g4, g5, g6, g7, g8) ->
                        g1 + ":" + g2 + ":" + g3 + ":" + g4 + ":"
                                + g5 + ":" + g6 + ":" + g7 + ":" + g8);
    }

    /**
     * Genera nombres de personas con 1-5 palabras alfabéticas.
     *
     * @return generador de nombres de personas
     */
    @Provide
    public static Arbitrary<String> personNames() {
        Arbitrary<String> word = Arbitraries.strings()
                .withCharRange('A', 'Z')
                .ofLength(1)
                .flatMap(first -> Arbitraries.strings()
                        .withCharRange('a', 'z')
                        .ofMinLength(1)
                        .ofMaxLength(10)
                        .map(rest -> first + rest));
        return word.list().ofMinSize(1).ofMaxSize(5)
                .map(words -> String.join(" ", words));
    }

    /**
     * Genera configuraciones MaskConfig con valores aleatorios válidos.
     *
     * @return generador de MaskConfig
     */
    @Provide
    public static Arbitrary<MaskConfig> maskConfigs() {
        Arbitrary<Character> maskChars = Arbitraries.of('*', '#', 'X', '•');
        Arbitrary<Integer> visibleStart = Arbitraries.integers().between(0, 5);
        Arbitrary<Integer> visibleEnd = Arbitraries.integers().between(0, 5);
        Arbitrary<CountryCode> countries = Arbitraries.of(CountryCode.values());
        return Combinators.combine(maskChars, visibleStart, visibleEnd, countries)
                .as((mc, vs, ve, c) -> MaskConfig.builder()
                        .maskChar(mc)
                        .visibleStart(vs)
                        .visibleEnd(ve)
                        .country(c)
                        .build());
    }

    /**
     * Genera strings de caos: unicode, emojis, caracteres de control, strings enormes.
     *
     * @return generador de strings caóticos
     */
    @Provide
    public static Arbitrary<String> chaosStrings() {
        return Arbitraries.oneOf(
                // Strings unicode variados
                Arbitraries.strings().ofMinLength(0).ofMaxLength(200),
                // Emojis y caracteres especiales
                Arbitraries.of("😀🎉🔥", "🇵🇪🇺🇸", "💳📧📱", "∀∃∈∉", "αβγδ",
                        "中文测试", "日本語テスト", "한국어", "العربية", "हिन्दी"),
                // Caracteres de control
                Arbitraries.of("\t\n\r", "\0\0\0", "\u0001\u0002\u0003",
                        "\u200B\u200C\u200D", "\uFEFF"),
                // Strings enormes
                Arbitraries.strings().withCharRange('a', 'z').ofMinLength(500).ofMaxLength(1000),
                // Strings con caracteres especiales mezclados
                Arbitraries.of("test@<script>alert('xss')</script>.com",
                        "'; DROP TABLE users; --", "../../etc/passwd",
                        "\r\n\r\nHTTP/1.1 200 OK", "null", "undefined", "NaN",
                        "true", "false", " ", "  \t  \n  "),
                // Strings vacíos y de un carácter
                Arbitraries.of("", " ", "a", "1", "@", ".", ":", "-")
        );
    }

    /**
     * Genera un valor aleatorio del enum MaskType.
     *
     * @return generador de MaskType aleatorio
     */
    @Provide
    public static Arbitrary<MaskType> randomMaskType() {
        return Arbitraries.of(MaskType.values());
    }
}
