package pe.edu.nova.java.libs.mask.utils.strategy;

import pe.edu.nova.java.libs.mask.utils.config.MaskConfig;
import pe.edu.nova.java.libs.mask.utils.exception.InvalidFormatException;
import pe.edu.nova.java.libs.mask.utils.result.MaskResult;

/**
 * Interfaz funcional del patrón Strategy para enmascaramiento de datos sensibles.
 * <p>
 * Cada implementación define la lógica de enmascaramiento para un tipo de dato
 * y/o país específico. La validación de nulos y vacíos la realiza {@code MaskEngine}
 * antes de invocar esta interfaz.
 */
@FunctionalInterface
public interface MaskStrategy {
    /**
     * Enmascara el valor dado según la configuración proporcionada.
     * El valor nunca será nulo ni vacío — esa validación la hace MaskEngine.
     *
     * @param value Valor original a enmascarar
     * @param config Configuración de enmascaramiento
     * @return Resultado del enmascaramiento
     * @throws InvalidFormatException si el valor no tiene el formato esperado
     */
    MaskResult mask(String value, MaskConfig config);
}
