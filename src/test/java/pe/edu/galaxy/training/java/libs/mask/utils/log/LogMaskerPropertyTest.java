package pe.edu.galaxy.training.java.libs.mask.utils.log;

import net.jqwik.api.*;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.generators.MaskTestGenerators;
import pe.edu.galaxy.training.java.libs.mask.utils.strategy.StrategyRegistry;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property tests para {@link LogMasker}.
 * Verifica propiedades P18 y P19.
 */
class LogMaskerPropertyTest {

    private final StrategyRegistry registry = StrategyRegistry.getDefault();

    /**
     * P18: Detección automática en logs.
     * ∀ texto que contiene un email e: maskAutoDetect retorna texto donde e está enmascarado.
     * <p>
     * <b>Validates: Requirements 13.2</b>
     */
    @Property(tries = 200)
    void deberiaDetectarYEnmascararEmailEnTexto(
            @ForAll("validEmails") String email) {
        String text = "Prefijo " + email + " Sufijo";
        String result = LogMasker.maskAutoDetect(text, CountryCode.GENERIC, registry);

        // El email original no debería estar presente (a menos que sea de 1 char username)
        int atIndex = email.indexOf('@');
        if (atIndex > 1) {
            assertFalse(result.contains(email),
                    "El email original debería estar enmascarado en: " + result);
        }
        // El dominio debería preservarse
        String domain = email.substring(atIndex);
        assertTrue(result.contains(domain),
                "El dominio debería preservarse: " + domain);
    }

    /**
     * P19: Preservación de texto circundante en logs.
     * ∀ texto = prefix + sensitiveData + suffix: preserva prefix y suffix intactos.
     * <p>
     * <b>Validates: Requirements 13.4</b>
     */
    @Property(tries = 200)
    void deberiaPreservarTextoCircundante(
            @ForAll("validEmails") String email,
            @ForAll("prefixes") String prefix,
            @ForAll("suffixes") String suffix) {
        String text = prefix + " " + email + " " + suffix;
        String result = LogMasker.maskAutoDetect(text, CountryCode.GENERIC, registry);

        assertTrue(result.startsWith(prefix + " "),
                "Prefijo debería preservarse: '" + prefix + "'");
        assertTrue(result.endsWith(" " + suffix),
                "Sufijo debería preservarse: '" + suffix + "'");
    }

    @Provide
    Arbitrary<String> validEmails() {
        return MaskTestGenerators.validEmails();
    }

    @Provide
    Arbitrary<String> prefixes() {
        return Arbitraries.of("LOG:", "INFO:", "ERROR:", "DEBUG:", "Mensaje:");
    }

    @Provide
    Arbitrary<String> suffixes() {
        return Arbitraries.of("completado", "exitoso", "fallido", "procesado", "registrado");
    }
}
