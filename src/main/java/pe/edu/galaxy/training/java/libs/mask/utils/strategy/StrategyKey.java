package pe.edu.galaxy.training.java.libs.mask.utils.strategy;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.MaskType;

/**
 * Clave compuesta para el registro de estrategias en StrategyRegistry.
 * Package-private — Usado internamente como clave del ConcurrentHashMap.
 *
 * @param type Tipo de enmascaramiento
 * @param country Código de país
 */
record StrategyKey(MaskType type, CountryCode country) {}
