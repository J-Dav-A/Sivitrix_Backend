package co.edu.uniquindio.sivitrix.common.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de las reglas de negocio puras (NIT, dinero, busqueda aproximada). */
class UtilidadesDominioTest {

    // ---------- NIT (SWR-32, SWR-35) ----------
    @Test
    void nitConDigitoCorrectoEsValido() {
        assertTrue(NitUtils.esValido("800197268-4"));
    }

    @Test
    void nitConDigitoIncorrectoEsInvalido() {
        assertFalse(NitUtils.esValido("800197268-5"));
    }

    @Test
    void digitoDeVerificacionSeCalculaCorrectamente() {
        assertEquals(8, NitUtils.calcularDigitoVerificacion("900123456"));
    }

    @Test
    void nitSinGuionSoloExigeEntre7y15Digitos() {
        assertTrue(NitUtils.esValido("900123456"));
        assertFalse(NitUtils.esValido("12345"));
        assertFalse(NitUtils.esValido("ABC1234567"));
        assertFalse(NitUtils.esValido(null));
    }

    // ---------- Dinero (SWR-20) ----------
    @Test
    void redondeoSiempreEsHaciaArriba() {
        assertEquals(new BigDecimal("10.01"), MoneyUtils.redondear(new BigDecimal("10.001")));
        assertEquals(new BigDecimal("1234.57"), MoneyUtils.redondear(new BigDecimal("1234.561")));
    }

    @Test
    void redondearNuloDaCero() {
        assertEquals(new BigDecimal("0.00"), MoneyUtils.redondear(null));
    }

    @Test
    void valorExactoNoCambia() {
        assertEquals(new BigDecimal("50.00"), MoneyUtils.redondear(new BigDecimal("50")));
    }

    // ---------- Busqueda aproximada ----------
    @Test
    void busquedaEncuentraPorPalabraParcial() {
        assertTrue(FuzzySearchUtils.coincideAproximado("Cauchos Mordaza", "caucho"));
        assertTrue(FuzzySearchUtils.coincideAproximado("Cauchos Mordaza", "mordaza cauchos"));
        assertTrue(FuzzySearchUtils.coincideAproximado("Cauchos Mordaza", "CAUCHOS"));
    }

    @Test
    void busquedaToleraErroresDeTipeo() {
        assertTrue(FuzzySearchUtils.coincideAproximado("Cauchos Mordaza", "cuacho"));
    }

    @Test
    void busquedaDescartaTextoSinRelacion() {
        assertFalse(FuzzySearchUtils.coincideAproximado("Cauchos Mordaza", "llanta"));
    }

    @Test
    void consultaVaciaCoincideConTodo() {
        assertTrue(FuzzySearchUtils.coincideAproximado("Cauchos Mordaza", ""));
    }
}
