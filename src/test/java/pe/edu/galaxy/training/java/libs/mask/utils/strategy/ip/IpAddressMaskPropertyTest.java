package pe.edu.galaxy.training.java.libs.mask.utils.strategy.ip;

import net.jqwik.api.*;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.config.MaskConfig;
import pe.edu.galaxy.training.java.libs.mask.utils.generators.MaskTestGenerators;
import pe.edu.galaxy.training.java.libs.mask.utils.result.MaskResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property tests para {@link IpAddressMaskStrategy}.
 * Verifica propiedades P15 y P16.
 */
class IpAddressMaskPropertyTest {

    private final IpAddressMaskStrategy strategy = new IpAddressMaskStrategy();
    private final MaskConfig defaultConfig = MaskConfig.builder().country(CountryCode.GENERIC).build();

    /**
     * P15: Enmascaramiento de IPv4.
     * ∀ IPv4 ip válida: muestra primer octeto, enmascara los 3 restantes.
     * <p>
     * <b>Validates: Requirements 10.1</b>
     */
    @Property(tries = 200)
    void ipv4DeberiaMostrarPrimerOcteto(
            @ForAll("validIpv4") String ip) {
        MaskResult result = strategy.mask(ip, defaultConfig);
        String masked = result.maskedValue();

        String[] originalOctets = ip.split("\\.");
        String[] maskedOctets = masked.split("\\.");

        assertEquals(4, maskedOctets.length, "Debería tener 4 octetos");

        // Primer octeto visible
        assertEquals(originalOctets[0], maskedOctets[0],
                "Primer octeto debería ser visible");

        // Octetos 2-4 enmascarados
        for (int i = 1; i < 4; i++) {
            for (char c : maskedOctets[i].toCharArray()) {
                assertEquals('*', c,
                        "Octeto " + (i + 1) + " debería estar enmascarado");
            }
            // Longitud del octeto enmascarado = longitud del original
            assertEquals(originalOctets[i].length(), maskedOctets[i].length(),
                    "Longitud del octeto enmascarado debería coincidir");
        }
    }

    /**
     * P16: Enmascaramiento de IPv6.
     * ∀ IPv6 ip válida: muestra primeros 2 grupos, enmascara los 6 restantes.
     * <p>
     * <b>Validates: Requirements 10.2</b>
     */
    @Property(tries = 200)
    void ipv6DeberiaMostrarPrimerosDosGrupos(
            @ForAll("validIpv6") String ip) {
        MaskResult result = strategy.mask(ip, defaultConfig);
        String masked = result.maskedValue();

        String[] originalGroups = ip.split(":");
        String[] maskedGroups = masked.split(":");

        assertEquals(8, maskedGroups.length, "Debería tener 8 grupos");

        // Primeros 2 grupos visibles
        assertEquals(originalGroups[0], maskedGroups[0], "Grupo 1 debería ser visible");
        assertEquals(originalGroups[1], maskedGroups[1], "Grupo 2 debería ser visible");

        // Grupos 3-8 enmascarados
        for (int i = 2; i < 8; i++) {
            for (char c : maskedGroups[i].toCharArray()) {
                assertEquals('*', c,
                        "Grupo " + (i + 1) + " debería estar enmascarado");
            }
        }
    }

    @Provide
    Arbitrary<String> validIpv4() {
        return MaskTestGenerators.validIpv4();
    }

    @Provide
    Arbitrary<String> validIpv6() {
        return MaskTestGenerators.validIpv6();
    }
}
