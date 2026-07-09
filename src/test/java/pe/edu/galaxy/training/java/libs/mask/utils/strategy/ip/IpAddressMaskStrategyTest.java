package pe.edu.galaxy.training.java.libs.mask.utils.strategy.ip;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import pe.edu.galaxy.training.java.libs.mask.utils.MaskType;
import pe.edu.galaxy.training.java.libs.mask.utils.config.MaskConfig;
import pe.edu.galaxy.training.java.libs.mask.utils.exception.InvalidFormatException;
import pe.edu.galaxy.training.java.libs.mask.utils.result.MaskResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link IpAddressMaskStrategy}.
 * Verifica enmascaramiento de IPv4, IPv6, BVA y manejo de errores.
 */
@DisplayName("IpAddressMaskStrategy — Enmascaramiento de direcciones IP")
class IpAddressMaskStrategyTest {

    private final IpAddressMaskStrategy strategy = new IpAddressMaskStrategy();
    private final MaskConfig defaultConfig = MaskConfig.defaults();

    @Nested
    @DisplayName("IPv4")
    class Ipv4Tests {

        /** Verifica enmascaramiento estándar de IPv4. */
        @Test
        void deberiaEnmascararIpv4() {
            MaskResult result = strategy.mask("192.168.1.100", defaultConfig);
            assertEquals("192.***.*.***", result.maskedValue());
        }

        /** Verifica que muestra el primer octeto. */
        @Test
        void deberiaMostrarPrimerOcteto() {
            String masked = strategy.mask("10.0.0.1", defaultConfig).maskedValue();
            assertTrue(masked.startsWith("10."));
        }

        /** Verifica que preserva los puntos. */
        @Test
        void deberiaPreservarPuntos() {
            String masked = strategy.mask("192.168.1.100", defaultConfig).maskedValue();
            assertEquals('.', masked.charAt(3));
        }
    }

    @Nested
    @DisplayName("IPv6")
    class Ipv6Tests {

        /** Verifica enmascaramiento estándar de IPv6. */
        @Test
        void deberiaEnmascararIpv6() {
            MaskResult result = strategy.mask("2001:0db8:85a3:0000:0000:8a2e:0370:7334", defaultConfig);
            assertEquals("2001:0db8:****:****:****:****:****:****", result.maskedValue());
        }

        /** Verifica que muestra los primeros 2 grupos. */
        @Test
        void deberiaMostrarPrimerosDosGrupos() {
            String masked = strategy.mask("2001:0db8:85a3:0000:0000:8a2e:0370:7334", defaultConfig).maskedValue();
            assertTrue(masked.startsWith("2001:0db8:"));
        }
    }

    @Nested
    @DisplayName("BVA — Análisis de valores límite")
    class BoundaryValueAnalysis {

        /** Octeto 0 — valor mínimo válido. */
        @Test
        void octetoCeroDeberiaSerValido() {
            MaskResult result = strategy.mask("0.0.0.0", defaultConfig);
            assertEquals("0.*.*.*", result.maskedValue());
        }

        /** Octeto 255 — valor máximo válido. */
        @Test
        void octeto255DeberiaSerValido() {
            MaskResult result = strategy.mask("255.255.255.255", defaultConfig);
            assertEquals("255.***.***. ***".replace(" ", ""), result.maskedValue());
        }

        /** Octeto 256 — fuera de rango, debería lanzar excepción. */
        @Test
        void octeto256DeberiaLanzarExcepcion() {
            assertThrows(InvalidFormatException.class,
                    () -> strategy.mask("256.0.0.1", defaultConfig));
        }
    }

    @Nested
    @DisplayName("Negativos — Formatos inválidos")
    class Negativos {

        /** IP inválida debería lanzar InvalidFormatException. */
        @Test
        void ipInvalidaDeberiaLanzarExcepcion() {
            InvalidFormatException ex = assertThrows(InvalidFormatException.class,
                    () -> strategy.mask("not-an-ip", defaultConfig));
            assertEquals(MaskType.IP_ADDRESS, ex.getMaskType());
        }

        /** IPv4 con solo 3 octetos debería lanzar excepción. */
        @Test
        void ipv4Con3OctetosDeberiaLanzarExcepcion() {
            assertThrows(InvalidFormatException.class,
                    () -> strategy.mask("192.168.1", defaultConfig));
        }

        /** IPv6 con solo 7 grupos debería lanzar excepción. */
        @Test
        void ipv6Con7GruposDeberiaLanzarExcepcion() {
            assertThrows(InvalidFormatException.class,
                    () -> strategy.mask("2001:0db8:85a3:0000:0000:8a2e:0370", defaultConfig));
        }
    }

    @Nested
    @DisplayName("Parameterized — Tabla de IPs")
    class ParameterizedTests {

        /** Verifica múltiples IPv4. */
        @ParameterizedTest(name = "input={0}, expected={1}")
        @CsvSource({
                "192.168.1.100, 192.***.*.***",
                "10.0.0.1, 10.*.*.*",
                "0.0.0.0, 0.*.*.*"
        })
        void deberiaEnmascararIpv4(String input, String expected) {
            assertEquals(expected, strategy.mask(input, defaultConfig).maskedValue());
        }
    }
}
