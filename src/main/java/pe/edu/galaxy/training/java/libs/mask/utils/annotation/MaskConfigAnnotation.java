package pe.edu.galaxy.training.java.libs.mask.utils.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configuración de enmascaramiento a nivel de clase.
 * <p>
 * Permite definir valores predeterminados de carácter de máscara y país
 * que aplican a todos los campos {@link Masked} de la clase, a menos que
 * el campo especifique su propio valor.
 * <p>
 * Nombrada {@code MaskConfigAnnotation} para evitar conflicto con la clase
 * {@link pe.edu.galaxy.training.java.libs.mask.utils.config.MaskConfig}.
 *
 * <pre>
 * &#64;MaskConfigAnnotation(maskChar = '#', country = "PE")
 * public class UserDto {
 *     &#64;Masked(type = MaskType.EMAIL)
 *     private String email;
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MaskConfigAnnotation {

    /**
     * Carácter de máscara predeterminado para todos los campos de la clase.
     *
     * @return Carácter de máscara (default: '*')
     */
    char maskChar() default '*';

    /**
     * Código de país ISO 3166-1 alpha-2 predeterminado para todos los campos de la clase.
     * Vacío significa usar el default del sistema.
     *
     * @return Código de país, o vacío para default del sistema
     */
    String country() default "";
}
