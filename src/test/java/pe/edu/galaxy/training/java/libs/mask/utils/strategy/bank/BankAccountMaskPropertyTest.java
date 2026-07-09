package pe.edu.galaxy.training.java.libs.mask.utils.strategy.bank;

import net.jqwik.api.*;

import pe.edu.galaxy.training.java.libs.mask.utils.CountryCode;
import pe.edu.galaxy.training.java.libs.mask.utils.config.MaskConfig;
import pe.edu.galaxy.training.java.libs.mask.utils.generators.MaskTestGenerators;
import pe.edu.galaxy.training.java.libs.mask.utils.result.MaskResult;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property tests para estrategias de enmascaramiento de cuentas bancarias.
 * Verifica propiedades P13 y P9.
 */
class BankAccountMaskPropertyTest {

    private final PeruBankAccountMaskStrategy peruStrategy = new PeruBankAccountMaskStrategy();
    private final IbanMaskStrategy ibanStrategy = new IbanMaskStrategy();

    /**
     * P13: Enmascaramiento de CCI peruano.
     * ∀ CCI peruano a (20 dígitos): muestra primeros 3 y últimos 4.
     * <p>
     * <b>Validates: Requirements 8.1, 8.2</b>
     */
    @Property(tries = 200)
    void cciPeruanoDeberiaMostrarPrimeros3YUltimos4(
            @ForAll("peruCci") String cci) {
        MaskConfig config = MaskConfig.builder().country(CountryCode.PE).build();
        MaskResult result = peruStrategy.mask(cci, config);
        String masked = result.maskedValue();

        // Primeros 3 dígitos visibles
        assertEquals(cci.substring(0, 3), masked.substring(0, 3),
                "Primeros 3 dígitos deberían ser visibles");

        // Últimos 4 dígitos visibles
        assertEquals(cci.substring(16), masked.substring(16),
                "Últimos 4 dígitos deberían ser visibles");

        // Dígitos intermedios enmascarados
        for (int i = 3; i < 16; i++) {
            assertEquals('*', masked.charAt(i),
                    "Dígito en posición " + i + " debería estar enmascarado");
        }

        assertEquals(cci.length(), masked.length());
    }

    /**
     * P13: Enmascaramiento de IBAN.
     * ∀ IBAN a: muestra primeros 4 caracteres y últimos 4.
     * <p>
     * <b>Validates: Requirements 8.1, 8.2</b>
     */
    @Property(tries = 200)
    void ibanDeberiaMostrarPrimeros4YUltimos4(
            @ForAll("validIban") String iban) {
        MaskConfig config = MaskConfig.builder().country(CountryCode.GENERIC).build();
        MaskResult result = ibanStrategy.mask(iban, config);
        String masked = result.maskedValue();

        // Primeros 4 caracteres alfanuméricos visibles
        assertEquals(iban.substring(0, 4), masked.substring(0, 4),
                "Primeros 4 caracteres deberían ser visibles");

        // Últimos 4 caracteres visibles
        assertEquals(iban.substring(iban.length() - 4), masked.substring(masked.length() - 4),
                "Últimos 4 caracteres deberían ser visibles");

        assertEquals(iban.length(), masked.length());
    }

    /**
     * P9: Preservación de separadores en CCI peruano.
     * <p>
     * <b>Validates: Requirements 8.4</b>
     */
    @Property(tries = 200)
    void deberiaPreservarSeparadoresEnCci(
            @ForAll("peruCci") String cci) {
        MaskConfig config = MaskConfig.builder().country(CountryCode.PE).build();
        MaskResult result = peruStrategy.mask(cci, config);
        String masked = result.maskedValue();

        for (int i = 0; i < cci.length(); i++) {
            char original = cci.charAt(i);
            if (!Character.isDigit(original)) {
                assertEquals(original, masked.charAt(i),
                        "Separador en posición " + i + " debería preservarse");
            }
        }
    }

    @Provide
    Arbitrary<String> peruCci() {
        return MaskTestGenerators.peruCci();
    }

    @Provide
    Arbitrary<String> validIban() {
        return MaskTestGenerators.validIban();
    }
}
