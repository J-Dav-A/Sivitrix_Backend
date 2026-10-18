package co.edu.uniquindio.sivitrix.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Calculos financieros del negocio, aislados para poder probarlos sin Spring.
 * Todos los resultados cumplen SWR-20: 2 decimales, redondeo hacia arriba.
 */
public final class FinanzasUtils {

    /** SWR-19: IVA del 19 %. */
    public static final BigDecimal TASA_IVA = new BigDecimal("0.19");

    private FinanzasUtils() {
    }

    public static BigDecimal calcularIva(BigDecimal subtotal) {
        return MoneyUtils.redondear(subtotal.multiply(TASA_IVA));
    }

    public static BigDecimal calcularTotal(BigDecimal subtotal) {
        BigDecimal base = MoneyUtils.redondear(subtotal);
        return MoneyUtils.redondear(base.add(calcularIva(base)));
    }

    /**
     * Divide un valor en n cuotas iguales redondeadas hacia arriba; la ultima
     * absorbe la diferencia para que la suma sea exactamente el total.
     */
    public static List<BigDecimal> dividirEnCuotas(BigDecimal total, int n) {
        if (n < 1) {
            throw new IllegalArgumentException("El numero de cuotas debe ser al menos 1");
        }
        BigDecimal base = total.divide(BigDecimal.valueOf(n), 2, RoundingMode.CEILING);
        BigDecimal ultima = total.subtract(base.multiply(BigDecimal.valueOf(n - 1L)));
        if (ultima.signum() <= 0) {
            throw new IllegalArgumentException("El valor no se puede dividir en " + n + " cuotas");
        }
        List<BigDecimal> cuotas = new ArrayList<>();
        for (int i = 1; i < n; i++) {
            cuotas.add(base);
        }
        cuotas.add(ultima);
        return cuotas;
    }
}