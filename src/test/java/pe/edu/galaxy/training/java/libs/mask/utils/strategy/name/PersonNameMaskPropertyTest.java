package pe.edu.galaxy.training.java.libs.mask.utils.strategy.name;

import net.jqwik.api.*;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.config.MaskConfig;
import pe.edu.galaxy.training.java.libs.mask.utils.generators.MaskTestGenerators;
import pe.edu.galaxy.training.java.libs.mask.utils.result.MaskResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property tests para {@link PersonNameMaskStrategy}.
 * Verifica propiedad P14.
 */
class PersonNameMaskPropertyTest {

    private final PersonNameMaskStrategy strategy = new PersonNameMaskStrategy();

    /**
     * P14: Enmascaramiento de nombres de personas.
     * ∀ nombre n con ≥ 1 palabra: cada palabra muestra solo la primera letra, el resto enmascarado.
     * <p>
     * <b>Validates: Requirements 9.1, 9.2</b>
     */
    @Property(tries = 200)
    void cadaPalabraDeberiaMostrarSoloPrimeraLetra(
            @ForAll("personNames") String name) {
        MaskConfig config = MaskConfig.builder().country(CountryCode.GENERIC).build();
        MaskResult result = strategy.mask(name, config);
        String masked = result.maskedValue();

        String[] originalWords = name.split(" ");
        String[] maskedWords = masked.split(" ");

        assertEquals(originalWords.length, maskedWords.length,
                "Cantidad de palabras debería preservarse");

        for (int w = 0; w < originalWords.length; w++) {
            String origWord = originalWords[w];
            String maskWord = maskedWords[w];

            assertEquals(origWord.length(), maskWord.length(),
                    "Longitud de palabra debería preservarse");

            // Primera letra visible
            assertEquals(origWord.charAt(0), maskWord.charAt(0),
                    "Primera letra de la palabra debería ser visible");

            // Resto enmascarado
            for (int i = 1; i < maskWord.length(); i++) {
                assertEquals('*', maskWord.charAt(i),
                        "Carácter en posición " + i + " de palabra '" + origWord + "' debería estar enmascarado");
            }
        }
    }

    @Provide
    Arbitrary<String> personNames() {
        return MaskTestGenerators.personNames();
    }
}
