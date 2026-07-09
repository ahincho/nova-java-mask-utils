package pe.edu.nova.java.libs.mask.utils.annotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import pe.edu.nova.java.libs.mask.utils.MaskType;
import pe.edu.nova.java.libs.mask.utils.strategy.StrategyRegistry;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link AnnotationProcessor}.
 * Verifica procesamiento de @Masked, inferencia de tipo, conversión y preservación.
 */
@DisplayName("AnnotationProcessor — Procesamiento de anotaciones")
class AnnotationProcessorTest {

    private final StrategyRegistry registry = StrategyRegistry.getDefault();

    /** Clase de prueba con campos anotados. */
    static class UserDto {
        @Masked(type = MaskType.EMAIL)
        String email = "john@gmail.com";

        @Masked(type = MaskType.PHONE, country = "PE")
        String phone = "+51 987 654 321";

        String name = "Juan Perez";
    }

    /** Clase de prueba para inferencia de tipo por nombre de campo. */
    static class InferenceDto {
        @Masked
        String email = "test@example.com";

        @Masked
        String phone = "+51 111 222 333";

        @Masked
        String dni = "12345678";
    }

    /** Clase de prueba sin anotaciones. */
    static class PlainDto {
        String name = "Juan";
        String value = "test";
    }

    /** Clase de prueba con campo no-String. */
    static class NonStringDto {
        @Masked(type = MaskType.IDENTITY_DOCUMENT, country = "PE")
        String document = "12345678";

        int age = 30;
    }

    /** Clase con @MaskConfigAnnotation a nivel de clase. */
    @MaskConfigAnnotation(maskChar = '#', country = "PE")
    static class ConfiguredDto {
        @Masked(type = MaskType.EMAIL)
        String email = "user@test.com";
    }

    @Nested
    @DisplayName("Procesamiento de @Masked")
    class ProcesamientoMasked {

        /** Verifica que campos @Masked se enmascaran correctamente. */
        @Test
        void deberiaEnmascararCamposAnotados() {
            UserDto dto = new UserDto();
            AnnotationProcessor.process(dto, registry);
            assertNotEquals("john@gmail.com", dto.email);
            assertTrue(dto.email.contains("@gmail.com"),
                    "Dominio debería preservarse");
        }

        /** Verifica que el teléfono se enmascara con país específico. */
        @Test
        void deberiaEnmascararTelefonoConPais() {
            UserDto dto = new UserDto();
            AnnotationProcessor.process(dto, registry);
            assertTrue(dto.phone.startsWith("+51"),
                    "Código de país debería preservarse");
            assertNotEquals("+51 987 654 321", dto.phone);
        }
    }

    @Nested
    @DisplayName("Inferencia de tipo por nombre de campo")
    class InferenciaTipo {

        /** Verifica que el tipo se infiere del nombre del campo 'email'. */
        @Test
        void deberiaInferirTipoEmail() {
            InferenceDto dto = new InferenceDto();
            AnnotationProcessor.process(dto, registry);
            assertTrue(dto.email.contains("@example.com"),
                    "Debería enmascarar como email preservando dominio");
        }

        /** Verifica que el tipo se infiere del nombre del campo 'dni'. */
        @Test
        void deberiaInferirTipoDni() {
            InferenceDto dto = new InferenceDto();
            AnnotationProcessor.process(dto, registry);
            assertNotEquals("12345678", dto.dni);
        }
    }

    @Nested
    @DisplayName("Campos no anotados preservados")
    class CamposNoAnotados {

        /** Verifica que campos sin @Masked no se modifican. */
        @Test
        void deberiaPreservarCamposNoAnotados() {
            UserDto dto = new UserDto();
            AnnotationProcessor.process(dto, registry);
            assertEquals("Juan Perez", dto.name,
                    "Campo sin @Masked no debería modificarse");
        }
    }

    @Nested
    @DisplayName("Objeto sin anotaciones")
    class ObjetoSinAnotaciones {

        /** Verifica que un objeto sin anotaciones no se modifica. */
        @Test
        void deberiaRetornarObjetoSinCambios() {
            PlainDto dto = new PlainDto();
            PlainDto result = AnnotationProcessor.process(dto, registry);
            assertEquals("Juan", result.name);
            assertEquals("test", result.value);
        }
    }

    @Nested
    @DisplayName("Manejo de nulos")
    class ManejoNulos {

        /** Null debería retornar null. */
        @Test
        void nullDeberiaRetornarNull() {
            assertNull(AnnotationProcessor.process(null, registry));
        }
    }

    @Nested
    @DisplayName("@MaskConfigAnnotation a nivel de clase")
    class ConfiguracionClase {

        /** Verifica que @MaskConfigAnnotation aplica maskChar a nivel de clase. */
        @Test
        void deberiaAplicarMaskCharDeClase() {
            ConfiguredDto dto = new ConfiguredDto();
            AnnotationProcessor.process(dto, registry);
            assertTrue(dto.email.contains("#"),
                    "Debería usar '#' como carácter de máscara");
        }
    }
}
