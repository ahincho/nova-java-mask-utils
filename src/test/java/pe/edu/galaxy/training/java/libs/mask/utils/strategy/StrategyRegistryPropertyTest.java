package pe.edu.galaxy.training.java.libs.mask.utils.strategy;

import net.jqwik.api.*;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.MaskType;
import pe.edu.galaxy.training.java.libs.mask.utils.config.MaskConfig;
import pe.edu.galaxy.training.java.libs.mask.utils.exception.UnsupportedMaskTypeException;
import pe.edu.galaxy.training.java.libs.mask.utils.result.MaskResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property tests para {@link StrategyRegistry}.
 * Verifica propiedades P3, P4, P5 y P17.
 */
class StrategyRegistryPropertyTest {

    /** Estrategia dummy para testing. */
    private static MaskStrategy dummyStrategy(String id) {
        return (value, config) ->
                new MaskResult(value, id, MaskType.EMAIL, CountryCode.GENERIC, true);
    }

    /**
     * P3: Registro y resolución de estrategias.
     * ∀ tipo t, país c, estrategia s: después de register(t, c, s) → resolve(t, c) = s.
     * <p>
     * <b>Validates: Requirements 2.3, 11.3</b>
     */
    @Property(tries = 200)
    void registroYResolucionDeEstrategias(
            @ForAll("maskTypes") MaskType type,
            @ForAll("countryCodes") CountryCode country) {
        StrategyRegistry registry = StrategyRegistry.create();
        MaskStrategy strategy = dummyStrategy("test");
        registry.register(type, country, strategy);
        MaskStrategy resolved = registry.resolve(type, country);
        assertSame(strategy, resolved);
    }

    /**
     * P4: Semántica de última escritura gana.
     * ∀ tipo t, país c, estrategias s1, s2: después de register(t,c,s1); register(t,c,s2) → resolve(t,c) = s2.
     * <p>
     * <b>Validates: Requirements 2.4, 2.5</b>
     */
    @Property(tries = 200)
    void semanticaDeUltimaEscrituraGana(
            @ForAll("maskTypes") MaskType type,
            @ForAll("countryCodes") CountryCode country) {
        StrategyRegistry registry = StrategyRegistry.create();
        MaskStrategy s1 = dummyStrategy("first");
        MaskStrategy s2 = dummyStrategy("second");
        registry.register(type, country, s1);
        registry.register(type, country, s2);
        MaskStrategy resolved = registry.resolve(type, country);
        assertSame(s2, resolved);
    }

    /**
     * P5: Completitud de estrategias predeterminadas.
     * ∀ tipo t ∈ MaskType.values(): getDefault().hasStrategy(t, GENERIC) = true.
     * <p>
     * <b>Validates: Requirements 2.6</b>
     */
    @Property(tries = 50)
    void completitudDeEstrategiasPredeterminadas(
            @ForAll("maskTypes") MaskType type) {
        assertTrue(StrategyRegistry.getDefault().hasStrategy(type, CountryCode.GENERIC),
                "Debería tener estrategia genérica para " + type);
    }

    /**
     * P17: Fallback a estrategia genérica.
     * ∀ tipo t, país c sin estrategia específica: si existe (t, GENERIC) → resolve retorna genérica.
     * Si no existe (t, GENERIC) → resolve lanza UnsupportedMaskTypeException.
     * <p>
     * <b>Validates: Requirements 11.4</b>
     */
    @Property(tries = 200)
    void fallbackAEstrategiaGenerica(
            @ForAll("maskTypes") MaskType type,
            @ForAll("nonGenericCountries") CountryCode country) {
        StrategyRegistry registry = StrategyRegistry.create();
        MaskStrategy generic = dummyStrategy("generic");
        registry.register(type, generic); // Registra con GENERIC

        // Resolver con país no-GENERIC debería hacer fallback a GENERIC
        MaskStrategy resolved = registry.resolve(type, country);
        assertSame(generic, resolved);
    }

    /**
     * P17 (caso negativo): Sin estrategia genérica → UnsupportedMaskTypeException.
     */
    @Property(tries = 100)
    void sinEstrategiaGenericaDeberiaLanzarExcepcion(
            @ForAll("maskTypes") MaskType type,
            @ForAll("countryCodes") CountryCode country) {
        StrategyRegistry registry = StrategyRegistry.create();
        assertThrows(UnsupportedMaskTypeException.class,
                () -> registry.resolve(type, country));
    }

    @Provide
    Arbitrary<MaskType> maskTypes() {
        return Arbitraries.of(MaskType.values());
    }

    @Provide
    Arbitrary<CountryCode> countryCodes() {
        return Arbitraries.of(CountryCode.values());
    }

    @Provide
    Arbitrary<CountryCode> nonGenericCountries() {
        return Arbitraries.of(CountryCode.PE, CountryCode.US);
    }
}
