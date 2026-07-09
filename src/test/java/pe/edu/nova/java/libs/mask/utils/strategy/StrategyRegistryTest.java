package pe.edu.nova.java.libs.mask.utils.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import pe.edu.nova.java.libs.mask.utils.CountryCode;
import pe.edu.nova.java.libs.mask.utils.MaskType;
import pe.edu.nova.java.libs.mask.utils.config.MaskConfig;
import pe.edu.nova.java.libs.mask.utils.exception.UnsupportedMaskTypeException;
import pe.edu.nova.java.libs.mask.utils.result.MaskResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link StrategyRegistry}.
 * Verifica registro, resolución, fallback y semántica de última escritura.
 */
@DisplayName("StrategyRegistry — Registro de estrategias")
class StrategyRegistryTest {

    /** Estrategia dummy para testing. */
    private static final MaskStrategy DUMMY = (value, config) ->
            new MaskResult(value, "masked", MaskType.EMAIL, CountryCode.GENERIC, true);

    private static final MaskStrategy DUMMY2 = (value, config) ->
            new MaskResult(value, "masked2", MaskType.EMAIL, CountryCode.GENERIC, true);

    @Nested
    @DisplayName("Registro y resolución")
    class RegistroResolucion {

        /** Verifica que se puede registrar y resolver una estrategia específica. */
        @Test
        void deberiaRegistrarYResolverEstrategia() {
            StrategyRegistry registry = StrategyRegistry.create();
            registry.register(MaskType.EMAIL, CountryCode.PE, DUMMY);
            MaskStrategy resolved = registry.resolve(MaskType.EMAIL, CountryCode.PE);
            assertSame(DUMMY, resolved);
        }

        /** Verifica que register(type, strategy) registra con GENERIC. */
        @Test
        void deberiaRegistrarConGenericPorDefecto() {
            StrategyRegistry registry = StrategyRegistry.create();
            registry.register(MaskType.EMAIL, DUMMY);
            MaskStrategy resolved = registry.resolve(MaskType.EMAIL, CountryCode.GENERIC);
            assertSame(DUMMY, resolved);
        }

        /** Verifica que hasStrategy retorna true para estrategia registrada. */
        @Test
        void deberiaRetornarTrueParaEstrategiaRegistrada() {
            StrategyRegistry registry = StrategyRegistry.create();
            registry.register(MaskType.PHONE, CountryCode.US, DUMMY);
            assertTrue(registry.hasStrategy(MaskType.PHONE, CountryCode.US));
        }

        /** Verifica que hasStrategy retorna false para estrategia no registrada. */
        @Test
        void deberiaRetornarFalseParaEstrategiaNoRegistrada() {
            StrategyRegistry registry = StrategyRegistry.create();
            assertFalse(registry.hasStrategy(MaskType.PHONE, CountryCode.US));
        }
    }

    @Nested
    @DisplayName("Fallback a GENERIC")
    class FallbackGeneric {

        /** Verifica que resolve hace fallback a GENERIC cuando no hay estrategia específica. */
        @Test
        void deberiaHacerFallbackAGeneric() {
            StrategyRegistry registry = StrategyRegistry.create();
            registry.register(MaskType.EMAIL, DUMMY);
            MaskStrategy resolved = registry.resolve(MaskType.EMAIL, CountryCode.PE);
            assertSame(DUMMY, resolved);
        }

        /** Verifica que hasStrategy retorna true vía fallback GENERIC. */
        @Test
        void hasStrategyDeberiaRetornarTrueViaFallback() {
            StrategyRegistry registry = StrategyRegistry.create();
            registry.register(MaskType.EMAIL, DUMMY);
            assertTrue(registry.hasStrategy(MaskType.EMAIL, CountryCode.US));
        }
    }

    @Nested
    @DisplayName("Semántica de última escritura gana")
    class UltimaEscritura {

        /** Verifica que la última estrategia registrada reemplaza la anterior. */
        @Test
        void deberiaReemplazarEstrategiaAnterior() {
            StrategyRegistry registry = StrategyRegistry.create();
            registry.register(MaskType.EMAIL, CountryCode.PE, DUMMY);
            registry.register(MaskType.EMAIL, CountryCode.PE, DUMMY2);
            MaskStrategy resolved = registry.resolve(MaskType.EMAIL, CountryCode.PE);
            assertSame(DUMMY2, resolved);
        }
    }

    @Nested
    @DisplayName("Negativos — Tipos no registrados")
    class Negativos {

        /** Verifica que resolve lanza UnsupportedMaskTypeException para tipo no registrado. */
        @Test
        void deberiaLanzarExcepcionParaTipoNoRegistrado() {
            StrategyRegistry registry = StrategyRegistry.create();
            assertThrows(UnsupportedMaskTypeException.class,
                    () -> registry.resolve(MaskType.EMAIL, CountryCode.GENERIC));
        }
    }

    @Nested
    @DisplayName("Registro predeterminado")
    class RegistroPredeterminado {

        /** Verifica que getDefault() tiene estrategias para todos los MaskType. */
        @Test
        void deberiaContenerEstrategiasParaTodosLosTipos() {
            StrategyRegistry registry = StrategyRegistry.getDefault();
            for (MaskType type : MaskType.values()) {
                assertTrue(registry.hasStrategy(type, CountryCode.GENERIC),
                        "Debería tener estrategia para " + type);
            }
        }
    }
}
