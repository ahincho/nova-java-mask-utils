package pe.edu.galaxy.training.java.libs.mask.utils.log;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.MaskType;
import pe.edu.galaxy.training.java.libs.mask.utils.strategy.StrategyRegistry;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link LogMasker}.
 * Verifica auto-detección, patrones explícitos y manejo de nulos.
 */
@DisplayName("LogMasker — Enmascaramiento de logs")
class LogMaskerTest {

    private final StrategyRegistry registry = StrategyRegistry.getDefault();
    private final CountryCode country = CountryCode.GENERIC;

    @Nested
    @DisplayName("Auto-detección")
    class AutoDeteccion {

        /** Verifica detección y enmascaramiento de email en texto. */
        @Test
        void deberiaDetectarYEnmascararEmail() {
            String text = "El usuario john.doe@gmail.com inició sesión";
            String result = LogMasker.maskAutoDetect(text, country, registry);
            assertFalse(result.contains("john.doe@gmail.com"),
                    "El email debería estar enmascarado");
            assertTrue(result.contains("@gmail.com"),
                    "El dominio debería preservarse");
        }

        /** Verifica detección de IPv4 en texto. */
        @Test
        void deberiaDetectarYEnmascararIpv4() {
            String text = "Conexión desde 192.168.1.100 exitosa";
            String result = LogMasker.maskAutoDetect(text, country, registry);
            assertFalse(result.contains("192.168.1.100"),
                    "La IP debería estar enmascarada");
            assertTrue(result.contains("192."),
                    "El primer octeto debería preservarse");
        }

        /** Verifica detección de tarjeta de crédito en texto. */
        @Test
        void deberiaDetectarYEnmascararTarjeta() {
            String text = "Pago con tarjeta 4532 0123 4567 8901";
            String result = LogMasker.maskAutoDetect(text, country, registry);
            assertFalse(result.contains("4532 0123 4567 8901"),
                    "La tarjeta debería estar enmascarada");
        }

        /** Verifica detección de teléfono con código de país. */
        @Test
        void deberiaDetectarYEnmascararTelefono() {
            String text = "Llamar al +51 987 654 321 para confirmar";
            String result = LogMasker.maskAutoDetect(text, CountryCode.PE, registry);
            assertFalse(result.contains("+51 987 654 321"),
                    "El teléfono debería estar enmascarado");
        }
    }

    @Nested
    @DisplayName("Patrones explícitos")
    class PatronesExplicitos {

        /** Verifica enmascaramiento con patrón regex explícito. */
        @Test
        void deberiaEnmascararConPatronExplicito() {
            String text = "DNI: 12345678 del cliente";
            Map<String, MaskType> patterns = Map.of("\\d{8}", MaskType.IDENTITY_DOCUMENT);
            String result = LogMasker.maskPatterns(text, patterns, CountryCode.PE, registry);
            assertFalse(result.contains("12345678"),
                    "El DNI debería estar enmascarado");
        }
    }

    @Nested
    @DisplayName("Preservación de texto circundante")
    class PreservacionTexto {

        /** Verifica que el texto antes y después del dato sensible se preserva. */
        @Test
        void deberiaPreservarTextoCircundante() {
            String text = "Inicio john@test.com Final";
            String result = LogMasker.maskAutoDetect(text, country, registry);
            assertTrue(result.startsWith("Inicio "),
                    "Texto antes debería preservarse");
            assertTrue(result.endsWith(" Final"),
                    "Texto después debería preservarse");
        }

        /** Verifica que texto sin patrones sensibles no se modifica. */
        @Test
        void textoSinPatronesDeberiaNoModificarse() {
            String text = "Este es un texto normal sin datos sensibles";
            String result = LogMasker.maskAutoDetect(text, country, registry);
            assertEquals(text, result);
        }
    }

    @Nested
    @DisplayName("Negativos — Manejo de nulos")
    class Negativos {

        /** Texto nulo debería retornar nulo. */
        @Test
        void textoNuloDeberiaRetornarNulo() {
            assertNull(LogMasker.maskAutoDetect(null, country, registry));
        }

        /** Texto nulo en maskPatterns debería retornar nulo. */
        @Test
        void textoNuloEnMaskPatternsDeberiaRetornarNulo() {
            assertNull(LogMasker.maskPatterns(null, Map.of(), country, registry));
        }
    }
}
