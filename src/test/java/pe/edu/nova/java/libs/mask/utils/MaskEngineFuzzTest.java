package pe.edu.nova.java.libs.mask.utils;

import net.jqwik.api.*;

import pe.edu.nova.java.libs.mask.utils.exception.InvalidFormatException;
import pe.edu.nova.java.libs.mask.utils.exception.UnsupportedMaskTypeException;
import pe.edu.nova.java.libs.mask.utils.generators.MaskTestGenerators;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de fuzzing / chaos para {@link MaskEngine}.
 * <p>
 * Alimenta strings caóticos (unicode, emojis, caracteres de control, strings enormes)
 * a todas las estrategias y verifica que solo se lanzan excepciones documentadas.
 * Nunca NullPointerException, StringIndexOutOfBoundsException ni StackOverflowError.
 */
class MaskEngineFuzzTest {

    /**
     * Fuzzing principal: chaosStrings × todos los MaskType.
     * Verifica que solo se lanzan excepciones documentadas.
     * Mínimo 500 iteraciones.
     */
    @Property(tries = 500)
    void nuncaDeberiaLanzarExcepcionNoDocumentada(
            @ForAll("chaosStrings") String input,
            @ForAll("randomMaskType") MaskType type) {
        try {
            MaskEngine.mask(input, type, CountryCode.GENERIC);
        } catch (InvalidFormatException | UnsupportedMaskTypeException e) {
            // Excepciones documentadas — OK
        } catch (NullPointerException e) {
            fail("NullPointerException no documentada para input='" + truncate(input) + "', type=" + type
                    + ": " + e.getMessage());
        } catch (StringIndexOutOfBoundsException e) {
            fail("StringIndexOutOfBoundsException no documentada para input='" + truncate(input) + "', type=" + type
                    + ": " + e.getMessage());
        } catch (StackOverflowError e) {
            fail("StackOverflowError para input='" + truncate(input) + "', type=" + type);
        } catch (Exception e) {
            fail("Excepción no documentada: " + e.getClass().getName()
                    + " para input='" + truncate(input) + "', type=" + type
                    + ": " + e.getMessage());
        }
    }

    /**
     * Fuzzing con país PE: verifica robustez con estrategias específicas de Perú.
     */
    @Property(tries = 500)
    void nuncaDeberiaLanzarExcepcionNoDocumentadaConPaisPe(
            @ForAll("chaosStrings") String input,
            @ForAll("randomMaskType") MaskType type) {
        try {
            MaskEngine.mask(input, type, CountryCode.PE);
        } catch (InvalidFormatException | UnsupportedMaskTypeException e) {
            // Excepciones documentadas — OK
        } catch (Exception e) {
            fail("Excepción no documentada con PE: " + e.getClass().getName()
                    + " para input='" + truncate(input) + "', type=" + type
                    + ": " + e.getMessage());
        }
    }

    /**
     * Fuzzing con país US: verifica robustez con estrategias específicas de US.
     */
    @Property(tries = 500)
    void nuncaDeberiaLanzarExcepcionNoDocumentadaConPaisUs(
            @ForAll("chaosStrings") String input,
            @ForAll("randomMaskType") MaskType type) {
        try {
            MaskEngine.mask(input, type, CountryCode.US);
        } catch (InvalidFormatException | UnsupportedMaskTypeException e) {
            // Excepciones documentadas — OK
        } catch (Exception e) {
            fail("Excepción no documentada con US: " + e.getClass().getName()
                    + " para input='" + truncate(input) + "', type=" + type
                    + ": " + e.getMessage());
        }
    }

    /** Trunca strings largos para mensajes de error legibles. */
    private String truncate(String s) {
        if (s == null) return "null";
        return s.length() > 50 ? s.substring(0, 50) + "..." : s;
    }

    @Provide
    Arbitrary<String> chaosStrings() {
        return MaskTestGenerators.chaosStrings();
    }

    @Provide
    Arbitrary<MaskType> randomMaskType() {
        return MaskTestGenerators.randomMaskType();
    }
}
