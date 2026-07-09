package pe.edu.nova.java.libs.mask.utils.strategy.identity;

import net.jqwik.api.*;

import pe.edu.nova.java.libs.mask.utils.CountryCode;
import pe.edu.nova.java.libs.mask.utils.config.MaskConfig;
import pe.edu.nova.java.libs.mask.utils.generators.MaskTestGenerators;
import pe.edu.nova.java.libs.mask.utils.result.MaskResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property tests para estrategias de enmascaramiento de documentos de identidad.
 * Verifica propiedades P6 y P7.
 */
class IdentityMaskPropertyTest {

    private final PeruIdentityMaskStrategy peruStrategy = new PeruIdentityMaskStrategy();
    private final UsIdentityMaskStrategy usStrategy = new UsIdentityMaskStrategy();
    private final GenericIdentityMaskStrategy genericStrategy = new GenericIdentityMaskStrategy();
    private final MaskConfig defaultConfig = MaskConfig.defaults();

    /**
     * P6: Enmascaramiento de documentos de identidad por país — DNI peruano.
     * ∀ DNI peruano d (8 dígitos): mask(d) muestra últimos 3 dígitos.
     * <p>
     * <b>Validates: Requirements 4.1, 4.2, 4.3</b>
     */
    @Property(tries = 200)
    void dniPeruanoDeberiasMostrarUltimosTresDigitos(
            @ForAll("peruDni") String dni) {
        MaskResult result = peruStrategy.mask(dni, defaultConfig);
        String masked = result.maskedValue();

        // Últimos 3 caracteres deben coincidir con el original
        assertEquals(dni.substring(5), masked.substring(5));
        // Primeros 5 deben ser '*'
        for (int i = 0; i < 5; i++) {
            assertEquals('*', masked.charAt(i));
        }
        assertEquals(dni.length(), masked.length());
    }

    /**
     * P6: Enmascaramiento de SSN estadounidense.
     * ∀ SSN s (formato XXX-XX-XXXX): mask(s) muestra últimos 4 dígitos, preserva guiones.
     * <p>
     * <b>Validates: Requirements 4.1, 4.2, 4.3</b>
     */
    @Property(tries = 200)
    void ssnUsDeberiasMostrarUltimosCuatroDigitos(
            @ForAll("usSsn") String ssn) {
        MaskResult result = usStrategy.mask(ssn, defaultConfig);
        String masked = result.maskedValue();

        // Guiones preservados
        assertEquals('-', masked.charAt(3));
        assertEquals('-', masked.charAt(6));
        // Últimos 4 dígitos visibles (posiciones 7-10)
        assertEquals(ssn.substring(7), masked.substring(7));
        assertEquals(ssn.length(), masked.length());
    }

    /**
     * P7: Valor corto completamente enmascarado.
     * ∀ documento d con length(d) ≤ visibleChars: retorna valor completamente enmascarado.
     * <p>
     * <b>Validates: Requirements 4.5</b>
     */
    @Property(tries = 200)
    void valorCortoDeberiaSerCompletamenteEnmascarado(
            @ForAll("shortDocuments") String doc) {
        MaskResult result = genericStrategy.mask(doc, defaultConfig);
        String masked = result.maskedValue();

        // Documentos de longitud ≤ 2 deben estar completamente enmascarados
        for (int i = 0; i < masked.length(); i++) {
            assertEquals('*', masked.charAt(i),
                    "Carácter en posición " + i + " debería estar enmascarado");
        }
    }

    @Provide
    Arbitrary<String> peruDni() {
        return MaskTestGenerators.peruDni();
    }

    @Provide
    Arbitrary<String> usSsn() {
        return MaskTestGenerators.usSsn();
    }

    /** Genera documentos cortos (longitud 1-2) para probar P7. */
    @Provide
    Arbitrary<String> shortDocuments() {
        return Arbitraries.strings()
                .withCharRange('A', 'Z')
                .ofMinLength(1)
                .ofMaxLength(2);
    }
}
