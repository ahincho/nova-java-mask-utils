package pe.edu.galaxy.training.java.libs.mask.utils.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excluye un campo o una clase completa del enmascaramiento automático.
 * <p>
 * A nivel de <b>campo</b>: el campo no se enmascara aunque su nombre coincida
 * con un patrón sensible conocido (email, telefono, dni, etc.).
 * </p>
 * <p>
 * A nivel de <b>clase</b>: ningún campo de la clase se enmascara automáticamente.
 * Los campos con {@link Masked} explícito siguen respetándose.
 * </p>
 *
 * <pre>
 * // Excluir un campo específico
 * public class Cliente {
 *     private String email;           // → se enmascara (por inferencia)
 *     &#64;SkipMasking
 *     private String nombre;          // → NO se enmascara
 * }
 *
 * // Excluir toda la clase
 * &#64;SkipMasking
 * public class ConfigDto {
 *     private String email;           // → NO se enmascara
 *     private String telefono;        // → NO se enmascara
 * }
 * </pre>
 *
 * @author Galaxy Training
 * @version 1.0.0
 * @see Masked
 * @see MaskedClass
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@Documented
public @interface SkipMasking {
}
