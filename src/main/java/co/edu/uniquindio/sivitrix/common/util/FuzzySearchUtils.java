package co.edu.uniquindio.sivitrix.common.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Busqueda "inteligente" tolerante a errores de tipeo y a coincidencias
 * parciales por palabra (ej. buscar "caucho" o "cuacho" encuentra
 * "Cauchos Mordaza"). No depende de extensiones de PostgreSQL.
 */
public final class FuzzySearchUtils {

    private static final Pattern DIACRITICOS = Pattern.compile("\\p{M}");
    private static final Pattern ESPACIOS = Pattern.compile("\\s+");

    private FuzzySearchUtils() {
    }

    public static boolean coincideAproximado(String textoOriginal, String consultaOriginal) {
        String consulta = normalizar(consultaOriginal);
        if (consulta.isBlank()) {
            return true;
        }
        String texto = normalizar(textoOriginal);

        String[] palabrasConsulta = ESPACIOS.split(consulta);
        String[] palabrasTexto = ESPACIOS.split(texto);

        for (String palabraConsulta : palabrasConsulta) {
            if (palabraConsulta.isBlank()) {
                continue;
            }
            boolean encontrada = false;
            for (String palabraTexto : palabrasTexto) {
                if (palabraTexto.contains(palabraConsulta) || esSimilar(palabraTexto, palabraConsulta)) {
                    encontrada = true;
                    break;
                }
            }
            if (!encontrada) {
                return false;
            }
        }
        return true;
    }

    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinAcentos = DIACRITICOS.matcher(Normalizer.normalize(texto, Normalizer.Form.NFD)).replaceAll("");
        return sinAcentos.toLowerCase().trim();
    }

    private static boolean esSimilar(String a, String b) {
        if (a.isEmpty() || b.isEmpty()) {
            return false;
        }
        int tolerancia = Math.max(1, Math.min(a.length(), b.length()) / 3);
        return distanciaEdicion(a, b) <= tolerancia;
    }

    private static int distanciaEdicion(String a, String b) {
        int la = a.length();
        int lb = b.length();
        int[][] dp = new int[la + 1][lb + 1];

        for (int i = 0; i <= la; i++) dp[i][0] = i;
        for (int j = 0; j <= lb; j++) dp[0][j] = j;

        for (int i = 1; i <= la; i++) {
            for (int j = 1; j <= lb; j++) {
                int costoSustitucion = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + costoSustitucion
                );
                if (i > 1 && j > 1
                        && a.charAt(i - 1) == b.charAt(j - 2)
                        && a.charAt(i - 2) == b.charAt(j - 1)) {
                    dp[i][j] = Math.min(dp[i][j], dp[i - 2][j - 2] + 1);
                }
            }
        }
        return dp[la][lb];
    }
}