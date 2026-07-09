package pe.edu.nova.java.libs.mask.utils;

/**
 * Enumeración de tipos de datos sensibles soportados por la librería mask-utils.
 */
public enum MaskType {
    /** Correo electrónico. */
    EMAIL,
    /** Número de teléfono. */
    PHONE,
    /** Documento de identidad (DNI, SSN, pasaporte). */
    IDENTITY_DOCUMENT,
    /** Tarjeta de crédito o débito. */
    CREDIT_CARD,
    /** Cuenta bancaria (CCI, IBAN). */
    BANK_ACCOUNT,
    /** Nombre de persona. */
    PERSON_NAME,
    /** Dirección IP (IPv4 o IPv6). */
    IP_ADDRESS
}
