package pe.edu.galaxy.training.java.libs.mask.utils.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca una clase para enmascaramiento automático de todos sus campos {@code String}.
 * <p>
 * Cuando una clase está anotada con {@code @MaskedClass}, el serializador de Jackson
 * tratará todos los campos {@code String} como si tuvieran {@link Masked}, infiriendo
 * el {@link pe.edu.galaxy.training.java.libs.mask.utils.MaskType} desde el nombre del campo.
 * Los campos que ya tengan {@code @Masked} explícito conservan su configuración.
 * </p>
 * <p>
 * Se puede combinar con {@link MaskConfigAnnotation} para definir el carácter de máscara
 * y el país predeterminado de la clase.
 * </p>
 *
 * <pre>
 * &#64;MaskedClass
 * public class Cliente {
 *     private String nombre;    // → infiere PERSON_NAME
 *     private String email;     // → infiere EMAIL
 *     private String telefono;  // → infiere PHONE
 *     private String dni;       // → infiere IDENTITY_DOCUMENT
 *     private int edad;         // → no se enmascara (no es String)
 * }
 * </pre>
 *
 * @author Galaxy Training
 * @version 1.0.0
 * @see Masked
 * @see MaskConfigAnnotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface MaskedClass {
}
