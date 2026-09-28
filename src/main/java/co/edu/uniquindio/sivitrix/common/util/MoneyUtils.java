package co.edu.uniquindio.sivitrix.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Regla de negocio SWR-20: todos los valores monetarios del sistema se
 * manejan con dos decimales, redondeando siempre al centavo inmediatamente
 * SUPERIOR (CEILING), nunca al mas cercano.
 */
public final class MoneyUtils {

    private MoneyUtils() {
    }

    public static BigDecimal redondear(BigDecimal valor) {
        if (valor == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.CEILING);
        }
        return valor.setScale(2, RoundingMode.CEILING);
    }
}
