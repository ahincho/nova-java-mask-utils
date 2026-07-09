package pe.edu.nova.java.libs.mask.utils.annotation;

import net.jqwik.api.*;

import pe.edu.nova.java.libs.mask.utils.MaskType;
import pe.edu.nova.java.libs.mask.utils.generators.MaskTestGenerators;
import pe.edu.nova.java.libs.mask.utils.strategy.StrategyRegistry;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property tests para {@link AnnotationProcessor}.
 * Verifica propiedad P20.
 */
class AnnotationProcessorPropertyTest {

    private final StrategyRegistry registry = StrategyRegistry.getDefault();

    /** Clase de prueba con campo @Masked de tipo EMAIL. */
    static class EmailHolder {
        @Masked(type = MaskType.EMAIL)
        String email;

        EmailHolder(String email) {
            this.email = email;
        }
    }

    /**
     * P20: Procesamiento de anotaciones.
     * ∀ objeto obj con campo @Masked(type=T): maskAnnotated(obj) retorna copia con ese campo
     * enmascarado según tipo T.
     * <p>
     * <b>Validates: Requirements 14.3</b>
     */
    @Property(tries = 200)
    void campoMaskedDeberiaSerEnmascarado(
            @ForAll("validEmails") String email) {
        EmailHolder holder = new EmailHolder(email);
        AnnotationProcessor.process(holder, registry);

        // El campo debería estar enmascarado (diferente del original si username > 1)
        int atIndex = email.indexOf('@');
        String domain = email.substring(atIndex);

        // Dominio preservado
        assertTrue(holder.email.endsWith(domain),
                "Dominio debería preservarse: " + domain);

        // Si username > 1 char, el valor debería ser diferente
        if (atIndex > 1) {
            assertNotEquals(email, holder.email,
                    "El email debería estar enmascarado");
        }

        // Longitud preservada
        assertEquals(email.length(), holder.email.length(),
                "Longitud debería preservarse");
    }

    @Provide
    Arbitrary<String> validEmails() {
        return MaskTestGenerators.validEmails();
    }
}
