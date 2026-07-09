package pe.edu.galaxy.training.java.libs.mask.utils.strategy.email;

import net.jqwik.api.*;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.config.MaskConfig;
import pe.edu.galaxy.training.java.libs.mask.utils.generators.MaskTestGenerators;
import pe.edu.galaxy.training.java.libs.mask.utils.result.MaskResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property tests para {@link EmailMaskStrategy}.
 * Verifica propiedades P10 y P11.
 */
class EmailMaskPropertyTest {

    private final EmailMaskStrategy strategy = new EmailMaskStrategy();

    /**
     * P10: Enmascaramiento de email.
     * ∀ email e con formato user@domain: preserva @domain completo y muestra primer carácter
     * de user (si len(user) > 1).
     * <p>
     * <b>Validates: Requirements 6.1, 6.2</b>
     */
    @Property(tries = 200)
    void deberiaPreservarDominioYMostrarPrimerCaracter(
            @ForAll("validEmails") String email) {
        MaskConfig config = MaskConfig.builder().country(CountryCode.GENERIC).build();
        MaskResult result = strategy.mask(email, config);
        String masked = result.maskedValue();

        int atIndex = email.indexOf('@');
        String domain = email.substring(atIndex);
        String username = email.substring(0, atIndex);

        // Dominio preservado
        assertTrue(masked.endsWith(domain),
                "Debería preservar dominio: " + domain);

        // Primer carácter del username
        if (username.length() > 1) {
            assertEquals(username.charAt(0), masked.charAt(0),
                    "Debería mostrar primer carácter del username");
        } else {
            assertEquals('*', masked.charAt(0),
                    "Username de 1 char debería estar enmascarado");
        }

        // Longitud preservada
        assertEquals(email.length(), masked.length());
    }

    /**
     * P11: Invariancia de tipos independientes del país.
     * ∀ email e, país c1, c2: mask(e, EMAIL, c1) = mask(e, EMAIL, c2).
     * <p>
     * <b>Validates: Requirements 6.4</b>
     */
    @Property(tries = 200)
    void deberiaSerIndependienteDelPais(
            @ForAll("validEmails") String email,
            @ForAll("countryCodes") CountryCode c1,
            @ForAll("countryCodes") CountryCode c2) {
        MaskConfig config1 = MaskConfig.builder().country(c1).build();
        MaskConfig config2 = MaskConfig.builder().country(c2).build();

        String masked1 = strategy.mask(email, config1).maskedValue();
        String masked2 = strategy.mask(email, config2).maskedValue();

        assertEquals(masked1, masked2,
                "El enmascaramiento de email debería ser independiente del país");
    }

    @Provide
    Arbitrary<String> validEmails() {
        return MaskTestGenerators.validEmails();
    }

    @Provide
    Arbitrary<CountryCode> countryCodes() {
        return Arbitraries.of(CountryCode.values());
    }
}
