package co.edu.uniquindio.sivitrix.common.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de IVA, total de venta y division de creditos en cuotas (F-01, F-05). */
class FinanzasUtilsTest {

    @Test
    void ivaEsElDiecinuevePorCiento() {
        assertEquals(new BigDecimal("161500.00"), FinanzasUtils.calcularIva(new BigDecimal("850000.00")));
    }

    @Test
    void ivaSeRedondeaHaciaArriba() {
        // 0.19 * 10.01 = 1.9019 -> 1.91
        assertEquals(new BigDecimal("1.91"), FinanzasUtils.calcularIva(new BigDecimal("10.01")));
    }

    @Test
    void totalEsSubtotalMasIva() {
        assertEquals(new BigDecimal("1011500.00"), FinanzasUtils.calcularTotal(new BigDecimal("850000.00")));
    }

    @Test
    void cuotasIgualesSumanExactamenteElTotal() {
        List<BigDecimal> cuotas = FinanzasUtils.dividirEnCuotas(new BigDecimal("1000.00"), 4);
        assertEquals(4, cuotas.size());
        assertEquals(new BigDecimal("250.00"), cuotas.get(0));
        assertEquals(new BigDecimal("1000.00"), cuotas.stream().reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Test
    void ultimaCuotaAbsorbeLaDiferenciaDeRedondeo() {
        // 100.00 / 3 = 33.34 (hacia arriba) -> 33.34 + 33.34 + 33.32
        List<BigDecimal> cuotas = FinanzasUtils.dividirEnCuotas(new BigDecimal("100.00"), 3);
        assertEquals(new BigDecimal("33.34"), cuotas.get(0));
        assertEquals(new BigDecimal("33.32"), cuotas.get(2));
        assertEquals(new BigDecimal("100.00"), cuotas.stream().reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Test
    void unaSolaCuotaEsElTotal() {
        List<BigDecimal> cuotas = FinanzasUtils.dividirEnCuotas(new BigDecimal("500.00"), 1);
        assertEquals(List.of(new BigDecimal("500.00")), cuotas);
    }

    @Test
    void cuotasInvalidasLanzanError() {
        assertThrows(IllegalArgumentException.class, () -> FinanzasUtils.dividirEnCuotas(new BigDecimal("100.00"), 0));
        assertThrows(IllegalArgumentException.class, () -> FinanzasUtils.dividirEnCuotas(new BigDecimal("0.10"), 36));
    }
}
