package co.edu.uniquindio.sivitrix.common.util;

import co.edu.uniquindio.sivitrix.cliente.entity.ClienteEmpresarial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/** CP-11: reglas del cupo de credito que el refactor H-02 movio a la entidad ClienteEmpresarial. */
class ClienteEmpresarialTest {

    private ClienteEmpresarial cliente;

    @BeforeEach
    void preparar() {
        cliente = ClienteEmpresarial.builder()
                .limiteCredito(new BigDecimal("1000000.00"))
                .saldoCredito(new BigDecimal("250000.00"))
                .build();
    }

    @Test
    void cupoDisponibleEsLimiteMenosSaldo() {
        assertEquals(new BigDecimal("750000.00"), cliente.cupoDisponible());
    }

    @Test
    void cupoDisponibleNuncaEsNegativo() {
        cliente.setSaldoCredito(new BigDecimal("1200000.00"));

        assertEquals(0, cliente.cupoDisponible().signum());
    }

    @Test
    void puedeFinanciarAceptaHastaElCupoExacto() {
        assertTrue(cliente.puedeFinanciar(new BigDecimal("750000.00")));
        assertFalse(cliente.puedeFinanciar(new BigDecimal("750000.01")));
    }

    @Test
    void puedeFinanciarRechazaCuandoElSaldoSuperaElLimite() {
        cliente.setSaldoCredito(new BigDecimal("1200000.00"));

        assertFalse(cliente.puedeFinanciar(new BigDecimal("1.00")));
    }

    @Test
    void aumentarSaldoSumaYRedondeaHaciaArriba() {
        cliente.aumentarSaldo(new BigDecimal("100000.005"));

        assertEquals(new BigDecimal("350000.01"), cliente.getSaldoCredito());
    }

    @Test
    void reducirSaldoRestaConDosDecimales() {
        cliente.reducirSaldo(new BigDecimal("100000.00"));

        assertEquals(new BigDecimal("150000.00"), cliente.getSaldoCredito());
    }

    @Test
    void reducirSaldoNuncaLoDejaNegativo() {
        cliente.reducirSaldo(new BigDecimal("500000.00"));

        assertEquals(new BigDecimal("0.00"), cliente.getSaldoCredito());
    }
}