package pe.edu.nova.java.libs.mask.utils.strategy;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import pe.edu.nova.java.libs.mask.utils.CountryCode;
import pe.edu.nova.java.libs.mask.utils.MaskType;
import pe.edu.nova.java.libs.mask.utils.exception.UnsupportedMaskTypeException;
import pe.edu.nova.java.libs.mask.utils.strategy.bank.GenericBankAccountMaskStrategy;
import pe.edu.nova.java.libs.mask.utils.strategy.bank.PeruBankAccountMaskStrategy;
import pe.edu.nova.java.libs.mask.utils.strategy.credit.CreditCardMaskStrategy;
import pe.edu.nova.java.libs.mask.utils.strategy.email.EmailMaskStrategy;
import pe.edu.nova.java.libs.mask.utils.strategy.identity.GenericIdentityMaskStrategy;
import pe.edu.nova.java.libs.mask.utils.strategy.identity.PeruIdentityMaskStrategy;
import pe.edu.nova.java.libs.mask.utils.strategy.identity.UsIdentityMaskStrategy;
import pe.edu.nova.java.libs.mask.utils.strategy.ip.IpAddressMaskStrategy;
import pe.edu.nova.java.libs.mask.utils.strategy.name.PersonNameMaskStrategy;
import pe.edu.nova.java.libs.mask.utils.strategy.phone.GenericPhoneMaskStrategy;
import pe.edu.nova.java.libs.mask.utils.strategy.phone.PeruPhoneMaskStrategy;
import pe.edu.nova.java.libs.mask.utils.strategy.phone.UsPhoneMaskStrategy;

/**
 * Registro thread-safe de estrategias de enmascaramiento.
 * <p>
 * Mapea combinaciones de {@link MaskType} y {@link CountryCode} a implementaciones
 * de {@link MaskStrategy}. La resolución sigue un orden de prioridad:
 * <ol>
 *   <li>Coincidencia exacta: {@code (type, country)}</li>
 *   <li>Fallback genérico: {@code (type, GENERIC)}</li>
 *   <li>Lanzar {@link UnsupportedMaskTypeException} si no hay coincidencia</li>
 * </ol>
 * <p>
 * Provee un singleton con estrategias predeterminadas vía {@link #getDefault()}
 * y registros vacíos para testing vía {@link #create()}.
 */
public final class StrategyRegistry {

    private final ConcurrentHashMap<StrategyKey, MaskStrategy> strategies;

    private StrategyRegistry() {
        this.strategies = new ConcurrentHashMap<>();
    }

    /**
     * Holder pattern para inicialización lazy y thread-safe del singleton.
     */
    private static final class DefaultHolder {
        static final StrategyRegistry INSTANCE = createDefault();

        private static StrategyRegistry createDefault() {
            StrategyRegistry registry = new StrategyRegistry();

            // Identidad
            registry.register(MaskType.IDENTITY_DOCUMENT, CountryCode.PE, new PeruIdentityMaskStrategy());
            registry.register(MaskType.IDENTITY_DOCUMENT, CountryCode.US, new UsIdentityMaskStrategy());
            registry.register(MaskType.IDENTITY_DOCUMENT, new GenericIdentityMaskStrategy());

            // Teléfono
            registry.register(MaskType.PHONE, CountryCode.PE, new PeruPhoneMaskStrategy());
            registry.register(MaskType.PHONE, CountryCode.US, new UsPhoneMaskStrategy());
            registry.register(MaskType.PHONE, new GenericPhoneMaskStrategy());

            // Email (Independiente del país)
            registry.register(MaskType.EMAIL, new EmailMaskStrategy());

            // Tarjeta de crédito (Independiente del país)
            registry.register(MaskType.CREDIT_CARD, new CreditCardMaskStrategy());

            // Cuenta bancaria
            registry.register(MaskType.BANK_ACCOUNT, CountryCode.PE, new PeruBankAccountMaskStrategy());
            registry.register(MaskType.BANK_ACCOUNT, new GenericBankAccountMaskStrategy());

            // Nombre de persona (Independiente del país)
            registry.register(MaskType.PERSON_NAME, new PersonNameMaskStrategy());

            // Dirección IP (Independiente del país)
            registry.register(MaskType.IP_ADDRESS, new IpAddressMaskStrategy());

            return registry;
        }
    }

    /**
     * Retorna el registro singleton con todas las estrategias predeterminadas.
     * La inicialización es lazy y thread-safe (Holder pattern).
     *
     * @return Registro singleton con estrategias predeterminadas
     */
    public static StrategyRegistry getDefault() {
        return DefaultHolder.INSTANCE;
    }

    /**
     * Crea un nuevo registro vacío, útil para testing o configuración personalizada.
     *
     * @return Nuevo registro vacío
     */
    public static StrategyRegistry create() {
        return new StrategyRegistry();
    }

    /**
     * Registra una estrategia para un tipo y país específicos.
     * Si ya existe una estrategia para la misma combinación, la reemplaza
     * (semántica de última escritura gana).
     *
     * @param type Tipo de enmascaramiento
     * @param country Código de país
     * @param strategy Estrategia a registrar
     * @throws NullPointerException si algún parámetro es nulo
     */
    public void register(MaskType type, CountryCode country, MaskStrategy strategy) {
        Objects.requireNonNull(type, "type no debe ser nulo");
        Objects.requireNonNull(country, "country no debe ser nulo");
        Objects.requireNonNull(strategy, "strategy no debe ser nulo");
        strategies.put(new StrategyKey(type, country), strategy);
    }

    /**
     * Registra una estrategia genérica (fallback) para un tipo.
     * Equivalente a {@code register(type, CountryCode.GENERIC, strategy)}.
     *
     * @param type Tipo de enmascaramiento
     * @param strategy Estrategia a registrar como fallback genérico
     * @throws NullPointerException si algún parámetro es nulo
     */
    public void register(MaskType type, MaskStrategy strategy) {
        register(type, CountryCode.GENERIC, strategy);
    }

    /**
     * Resuelve la estrategia para un tipo y país dados.
     * <p>
     * Orden de resolución:
     * <ol>
     *   <li>Coincidencia exacta: {@code (type, country)}</li>
     *   <li>Fallback genérico: {@code (type, GENERIC)}</li>
     *   <li>Lanzar {@link UnsupportedMaskTypeException}</li>
     * </ol>
     *
     * @param type Tipo de enmascaramiento
     * @param country Código de país
     * @return Estrategia encontrada
     * @throws UnsupportedMaskTypeException si no se encuentra estrategia ni fallback
     * @throws NullPointerException si algún parámetro es nulo
     */
    public MaskStrategy resolve(MaskType type, CountryCode country) {
        Objects.requireNonNull(type, "type no debe ser nulo");
        Objects.requireNonNull(country, "country no debe ser nulo");
        // 1. Coincidencia exacta
        MaskStrategy strategy = strategies.get(new StrategyKey(type, country));
        if (strategy != null) {
            return strategy;
        }
        // 2. Fallback a GENERIC
        if (country != CountryCode.GENERIC) {
            strategy = strategies.get(new StrategyKey(type, CountryCode.GENERIC));
            if (strategy != null) {
                return strategy;
            }
        }
        // 3. No encontrada
        throw new UnsupportedMaskTypeException(
                String.format("No se encontró estrategia registrada para type=%s, country=%s", type, country),
                type,
                country
        );
    }

    /**
     * Verifica si existe una estrategia para la combinación dada.
     * Retorna {@code true} si hay coincidencia exacta O fallback genérico.
     *
     * @param type Tipo de enmascaramiento
     * @param country Código de país
     * @return {@code true} Si existe una estrategia aplicable
     * @throws NullPointerException si algún parámetro es nulo
     */
    public boolean hasStrategy(MaskType type, CountryCode country) {
        Objects.requireNonNull(type, "type no debe ser nulo");
        Objects.requireNonNull(country, "country no debe ser nulo");
        if (strategies.containsKey(new StrategyKey(type, country))) {
            return true;
        }
        if (country != CountryCode.GENERIC) {
            return strategies.containsKey(new StrategyKey(type, CountryCode.GENERIC));
        }
        return false;
    }
}
