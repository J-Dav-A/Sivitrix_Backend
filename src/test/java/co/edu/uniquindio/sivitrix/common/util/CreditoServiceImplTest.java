package co.edu.uniquindio.sivitrix.common.util;

import co.edu.uniquindio.sivitrix.cliente.entity.ClienteEmpresarial;
import co.edu.uniquindio.sivitrix.cliente.repository.ClienteEmpresarialRepository;
import co.edu.uniquindio.sivitrix.common.exception.BusinessRuleException;
import co.edu.uniquindio.sivitrix.credito.dto.CreditoResponse;
import co.edu.uniquindio.sivitrix.credito.entity.Credito;
import co.edu.uniquindio.sivitrix.credito.entity.Cuota;
import co.edu.uniquindio.sivitrix.credito.entity.EstadoCredito;
import co.edu.uniquindio.sivitrix.credito.entity.EstadoCuota;
import co.edu.uniquindio.sivitrix.credito.repository.CreditoRepository;
import co.edu.uniquindio.sivitrix.credito.repository.CuotaRepository;
import co.edu.uniquindio.sivitrix.credito.service.CreditoServiceImpl;
import co.edu.uniquindio.sivitrix.venta.entity.Venta;
import co.edu.uniquindio.sivitrix.venta.service.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas de caracterizacion de CreditoServiceImpl (taller de refactorizacion, hallazgos H-02 y H-05).
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CreditoServiceImplTest {

    @Mock private CreditoRepository creditoRepository;
    @Mock private CuotaRepository cuotaRepository;
    @Mock private ClienteEmpresarialRepository clienteRepository;
    @Mock private VentaService ventaService;

    @InjectMocks private CreditoServiceImpl service;

    private ClienteEmpresarial cliente;
    private Credito credito;

    @BeforeEach
    void preparar() {
        cliente = ClienteEmpresarial.builder()
                .id(1L).nit("900123456-8").nombre("Taller Moto Express")
                .limiteCredito(new BigDecimal("1000000.00"))
                .saldoCredito(new BigDecimal("0.00"))
                .build();

        Venta venta = new Venta();
        venta.setId(5L);
        venta.setNumero("VIT-2026-00001");
        venta.setCliente(cliente);

        credito = new Credito();
        credito.setId(3L);
        credito.setVenta(venta);
        credito.setCliente(cliente);
        credito.setValorTotal(new BigDecimal("238000.00"));
        credito.setNumeroCuotas(2);
        credito.setEstado(EstadoCredito.PENDIENTE_AUTORIZACION);

        when(creditoRepository.findById(3L)).thenReturn(Optional.of(credito));
        when(creditoRepository.save(any(Credito.class))).thenAnswer(inv -> inv.getArgument(0));
        when(clienteRepository.save(any(ClienteEmpresarial.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cuotaRepository.save(any(Cuota.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    // CP-05
    @Test
    void autorizarApruebaGeneraCuotasSubeElSaldoYConfirmaLaVenta() {
        CreditoResponse r = service.autorizar(3L);

        assertEquals(EstadoCredito.APROBADO, r.estado());
        assertEquals(2, credito.getCuotas().size());
        assertEquals(new BigDecimal("119000.00"), credito.getCuotas().get(0).getValor());
        assertEquals(new BigDecimal("119000.00"), credito.getCuotas().get(1).getValor());
        assertEquals(new BigDecimal("238000.00"), cliente.getSaldoCredito());
        verify(ventaService).confirmarTrasAutorizacion(5L);
    }

    // CP-06
    @Test
    void autorizarConCupoInsuficienteSeRechazaConMensaje() {
        cliente.setLimiteCredito(new BigDecimal("100000.00"));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.autorizar(3L));

        assertEquals("No se puede autorizar: el credito (238000.00) excede el cupo disponible del cliente (100000.00)",
                ex.getMessage());
        verify(ventaService, never()).confirmarTrasAutorizacion(any());
        assertEquals(new BigDecimal("0.00"), cliente.getSaldoCredito());
    }

    @Test
    void autorizarUnCreditoYaResueltoSeRechaza() {
        credito.setEstado(EstadoCredito.APROBADO);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.autorizar(3L));

        assertTrue(ex.getMessage().contains("ya fue resuelto"), ex.getMessage());
    }

    private Cuota cuota(long id, int numero, String valor) {
        Cuota c = new Cuota();
        c.setId(id);
        c.setCredito(credito);
        c.setNumero(numero);
        c.setValor(new BigDecimal(valor));
        c.setFechaLimite(LocalDate.now().plusDays(30L * numero));
        c.setEstado(EstadoCuota.PENDIENTE);
        credito.getCuotas().add(c);
        return c;
    }

    // CP-07
    @Test
    void pagarCuotaBajaElSaldoYMarcaLaCuota() {
        credito.setEstado(EstadoCredito.APROBADO);
        cliente.setSaldoCredito(new BigDecimal("238000.00"));
        Cuota primera = cuota(9L, 1, "119000.00");
        cuota(10L, 2, "119000.00");
        when(cuotaRepository.findById(9L)).thenReturn(Optional.of(primera));

        service.pagarCuota(9L);

        assertEquals(EstadoCuota.PAGADA, primera.getEstado());
        assertEquals(new BigDecimal("119000.00"), cliente.getSaldoCredito());
        assertEquals(EstadoCredito.APROBADO, credito.getEstado());
    }

    // CP-07 (el saldo nunca queda negativo)
    @Test
    void pagarUnaCuotaMayorAlSaldoLoDejaEnCero() {
        credito.setEstado(EstadoCredito.APROBADO);
        cliente.setSaldoCredito(new BigDecimal("50000.00"));
        Cuota primera = cuota(9L, 1, "119000.00");
        cuota(10L, 2, "119000.00");
        when(cuotaRepository.findById(9L)).thenReturn(Optional.of(primera));

        service.pagarCuota(9L);

        assertEquals(new BigDecimal("0.00"), cliente.getSaldoCredito());
    }

    @Test
    void pagarLaUltimaCuotaMarcaElCreditoComoPagado() {
        credito.setEstado(EstadoCredito.APROBADO);
        cliente.setSaldoCredito(new BigDecimal("119000.00"));
        Cuota unica = cuota(9L, 1, "119000.00");
        when(cuotaRepository.findById(9L)).thenReturn(Optional.of(unica));

        service.pagarCuota(9L);

        assertEquals(EstadoCredito.PAGADO, credito.getEstado());
    }

    @Test
    void noSePuedePagarUnaCuotaDeUnCreditoNoAprobado() {
        Cuota primera = cuota(9L, 1, "119000.00");
        when(cuotaRepository.findById(9L)).thenReturn(Optional.of(primera));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.pagarCuota(9L));

        assertEquals("Solo se pueden pagar cuotas de un credito aprobado", ex.getMessage());
    }
}