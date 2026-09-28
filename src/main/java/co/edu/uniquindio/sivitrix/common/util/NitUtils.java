package co.edu.uniquindio.sivitrix.common.util;

/**
 * Calculo del digito de verificacion del NIT segun el algoritmo oficial
 * de la DIAN (Colombia). Se usa tanto para Cliente empresarial (SWR-35)
 * como para Proveedor (SWR-32), por eso vive en el paquete comun.
 */
public final class NitUtils {

    private static final int[] PESOS = {
            3, 7, 13, 17, 19, 23, 29, 37, 41, 43, 47, 53, 59, 67, 71
    };

    private NitUtils() {
    }

    /**
     * Calcula el digito de verificacion para un NIT (sin el digito),
     * solo con caracteres numericos.
     */
    public static int calcularDigitoVerificacion(String nitSinDigito) {
        String digitos = nitSinDigito.replaceAll("\\D", "");
        int suma = 0;
        int pesoIndex = 0;
        for (int i = digitos.length() - 1; i >= 0 && pesoIndex < PESOS.length; i--, pesoIndex++) {
            int digito = Character.getNumericValue(digitos.charAt(i));
            suma += digito * PESOS[pesoIndex];
        }
        int residuo = suma % 11;
        return (residuo <= 1) ? residuo : 11 - residuo;
    }

    /**
     * Valida un NIT con el formato "numero-digitoVerificacion" (ej. 900123456-7).
     * Si no trae el digito de verificacion, solo valida que sean 7 a 15
     * digitos numericos.
     */
    public static boolean esValido(String nit) {
        if (nit == null || nit.isBlank()) {
            return false;
        }
        String limpio = nit.trim();

        if (limpio.contains("-")) {
            String[] partes = limpio.split("-");
            if (partes.length != 2 || !partes[0].matches("\\d{7,15}") || !partes[1].matches("\\d")) {
                return false;
            }
            int digitoEsperado = calcularDigitoVerificacion(partes[0]);
            return digitoEsperado == Integer.parseInt(partes[1]);
        }

        return limpio.matches("\\d{7,15}");
    }
}
