package pe.edu.galaxy.training.java.libs.mask.utils.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pe.edu.galaxy.training.java.libs.mask.utils.MaskType;

/**
 * Marca un campo para enmascaramiento automático por {@link AnnotationProcessor}.
 * <p>
 * El tipo de enmascaramiento puede especificarse explícitamente o inferirse
 * del nombre del campo (por ejemplo, "email" → {@link MaskType#EMAIL}).
 * <p>
 * El país puede especificarse como código ISO 3166-1 alpha-2 (por ejemplo, "PE", "US").
 * Si se deja vacío, se usa el país de la clase ({@link MaskConfigAnnotation}) o el del sistema.
 *
 * <pre>
 * public class UserDto {
 *     &#64;Masked(type = MaskType.EMAIL)
 *     private String email;
 *
 *     &#64;Masked(type = MaskType.PHONE, country = "PE")
 *     private String phone;
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Masked {

    /**
     * Tipo de enmascaramiento a aplicar.
     * Si se deja como default ({@link MaskType#EMAIL}), el procesador intentará
     * inferir el tipo del nombre del campo.
     *
     * @return Tipo de enmascaramiento
     */
    MaskType type() default MaskType.EMAIL;

    /**
     * Código de país ISO 3166-1 alpha-2 para resolución de estrategia.
     * Vacío significa usar el default de la clase o del sistema.
     *
     * @return Código de país, o vacío para default
     */
    String country() default "";
}
