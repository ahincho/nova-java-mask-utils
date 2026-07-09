package pe.edu.nova.java.libs.mask.utils.strategy.ip;

import pe.edu.nova.java.libs.mask.utils.MaskType;
import pe.edu.nova.java.libs.mask.utils.config.MaskConfig;
import pe.edu.nova.java.libs.mask.utils.exception.InvalidFormatException;
import pe.edu.nova.java.libs.mask.utils.result.MaskResult;
import pe.edu.nova.java.libs.mask.utils.strategy.MaskStrategy;

/**
 * Estrategia de enmascaramiento para direcciones IP (IPv4 e IPv6).
 * <p>
 * IPv4: Muestra el primer octeto, enmascara los 3 restantes, preserva puntos.
 * Cada octeto enmascarado se reemplaza con caracteres de máscara que coinciden
 * con la longitud del octeto original.
 * <ul>
 *   <li>"192.168.1.100" → "192.***.*.***"</li>
 * </ul>
 * <p>
 * IPv6: Muestra los primeros 2 grupos, enmascara los 6 restantes, preserva dos puntos.
 * Cada grupo enmascarado se reemplaza con 4 caracteres de máscara.
 * <ul>
 *   <li>"2001:0db8:85a3:0000:0000:8a2e:0370:7334" → "2001:0db8:****:****:****:****:****:****"</li>
 * </ul>
 * <p>
 * Lanza {@link InvalidFormatException} para IPs inválidas.
 * Independiente del país.
 */
public class IpAddressMaskStrategy implements MaskStrategy {

    /** Crea una nueva instancia de {@code IpAddressMaskStrategy}. */
    public IpAddressMaskStrategy() {}

    private static final int IPV4_VISIBLE_OCTETS = 1;
    private static final int IPV4_TOTAL_OCTETS = 4;
    private static final int IPV6_VISIBLE_GROUPS = 2;
    private static final int IPV6_TOTAL_GROUPS = 8;

    @Override
    public MaskResult mask(String value, MaskConfig config) {
        char maskChar = config.maskChar();

        if (value.contains(":")) {
            return maskIpv6(value, maskChar, config);
        } else if (value.contains(".")) {
            return maskIpv4(value, maskChar, config);
        } else {
            throw new InvalidFormatException(
                    "Formato de dirección IP inválido: " + value,
                    MaskType.IP_ADDRESS,
                    config.country()
            );
        }
    }

    /**
     * Enmascara una dirección IPv4 mostrando el primer octeto y enmascarando los restantes.
     *
     * @param value Dirección IPv4 a enmascarar
     * @param maskChar Carácter de máscara a utilizar
     * @param config Configuración de enmascaramiento
     * @return Resultado del enmascaramiento
     * @throws InvalidFormatException si la dirección IPv4 es inválida
     */
    private MaskResult maskIpv4(String value, char maskChar, MaskConfig config) {
        String[] octets = value.split("\\.", -1);

        if (octets.length != IPV4_TOTAL_OCTETS) {
            throw new InvalidFormatException(
                    "Dirección IPv4 inválida: se esperaban 4 octetos, se recibieron " + octets.length,
                    MaskType.IP_ADDRESS,
                    config.country()
            );
        }

        // Validar que cada octeto sea un número válido entre 0-255
        for (String octet : octets) {
            try {
                int val = Integer.parseInt(octet);
                if (val < 0 || val > 255) {
                    throw new InvalidFormatException(
                            "Dirección IPv4 inválida: octeto fuera de rango: " + octet,
                            MaskType.IP_ADDRESS,
                            config.country()
                    );
                }
            } catch (NumberFormatException e) {
                throw new InvalidFormatException(
                        "Dirección IPv4 inválida: octeto no numérico: " + octet,
                        MaskType.IP_ADDRESS,
                        config.country(),
                        e
                );
            }
        }

        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < octets.length; i++) {
            if (i > 0) {
                masked.append('.');
            }
            if (i < IPV4_VISIBLE_OCTETS) {
                masked.append(octets[i]);
            } else {
                // Enmascarar con caracteres que coincidan con la longitud del octeto
                for (int j = 0; j < octets[i].length(); j++) {
                    masked.append(maskChar);
                }
            }
        }

        return new MaskResult(
                value,
                masked.toString(),
                MaskType.IP_ADDRESS,
                config.country(),
                true
        );
    }

    /**
     * Enmascara una dirección IPv6 mostrando los primeros 2 grupos y enmascarando los restantes.
     *
     * @param value Dirección IPv6 a enmascarar
     * @param maskChar Carácter de máscara a utilizar
     * @param config Configuración de enmascaramiento
     * @return Resultado del enmascaramiento
     * @throws InvalidFormatException si la dirección IPv6 es inválida
     */
    private MaskResult maskIpv6(String value, char maskChar, MaskConfig config) {
        String[] groups = value.split(":", -1);

        if (groups.length != IPV6_TOTAL_GROUPS) {
            throw new InvalidFormatException(
                    "Dirección IPv6 inválida: se esperaban 8 grupos, se recibieron " + groups.length,
                    MaskType.IP_ADDRESS,
                    config.country()
            );
        }

        // Validar que cada grupo sea hexadecimal válido (1-4 caracteres)
        for (String group : groups) {
            if (group.isEmpty() || group.length() > 4) {
                throw new InvalidFormatException(
                        "Dirección IPv6 inválida: grupo inválido: " + group,
                        MaskType.IP_ADDRESS,
                        config.country()
                );
            }
            try {
                Integer.parseInt(group, 16);
            } catch (NumberFormatException e) {
                throw new InvalidFormatException(
                        "Dirección IPv6 inválida: grupo no hexadecimal: " + group,
                        MaskType.IP_ADDRESS,
                        config.country(),
                        e
                );
            }
        }

        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < groups.length; i++) {
            if (i > 0) {
                masked.append(':');
            }
            if (i < IPV6_VISIBLE_GROUPS) {
                masked.append(groups[i]);
            } else {
                // Enmascarar cada grupo con caracteres de máscara
                for (int j = 0; j < groups[i].length(); j++) {
                    masked.append(maskChar);
                }
            }
        }

        return new MaskResult(
                value,
                masked.toString(),
                MaskType.IP_ADDRESS,
                config.country(),
                true
        );
    }
}
