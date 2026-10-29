package co.edu.uniquindio.sivitrix.common.util;

import co.edu.uniquindio.sivitrix.cliente.entity.ClienteEmpresarial;
import co.edu.uniquindio.sivitrix.cliente.repository.ClienteEmpresarialRepository;
import co.edu.uniquindio.sivitrix.common.exception.BusinessRuleException;
import co.edu.uniquindio.sivitrix.credito.entity.Credito;
import co.edu.uniquindio.sivitrix.credito.entity.Cuota;
import co.edu.uniquindio.sivitrix.credito.entity.EstadoCredito;
import co.edu.uniquindio.sivitrix.credito.entity.EstadoCuota;
import co.edu.uniquindio.sivitrix.credito.repository.CreditoRepository;
import co.edu.uniquindio.sivitrix.inventario.service.InventarioService;
import co.edu.uniquindio.sivitrix.producto.entity.Producto;
import co.edu.uniquindio.sivitrix.producto.repository.ProductoRepository;
import co.edu.uniquindio.sivitrix.venta.dto.VentaRequest;
import co.edu.uniquindio.sivitrix.venta.dto.VentaResponse;
import co.edu.uniquindio.sivitrix.venta.entity.DetalleVenta;
import co.edu.uniquindio.sivitrix.venta.entity.EstadoVenta;
import co.edu.uniquindio.sivitrix.venta.entity.Factura;
import co.edu.uniquindio.sivitrix.venta.entity.TipoPago;
import co.edu.uniquindio.sivitrix.venta.entity.Venta;
import co.edu.uniquindio.sivitrix.venta.repository.FacturaRepository;
import co.edu.uniquindio.sivitrix.venta.repository.VentaRepository;
import co.edu.uniquindio.sivitrix.venta.service.VentaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas de caracterizacion de VentaServiceImpl (taller de refactorizacion, hallazgos H-02 y H-05).
 * Fijan el comportamiento observable de registrar y cancelar para comprobar que el refactor lo conserva.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VentaServiceImplTest {

    @Mock private VentaRepository ventaRepository;
    @Mock private FacturaRepository facturaRepository;
    @Mock private ClienteEmpresarialRepository clienteRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private CreditoRepository creditoRepository;
    @Mock private InventarioService inventarioService;

    @InjectMocks private VentaServiceImpl service;

    private ClienteEmpresarial cliente;
    private Producto producto;

    @BeforeEach
    void preparar() {
        cliente = ClienteEmpresarial.builder()
                .id(1L).nit("900123456-8").nombre("Taller Moto Express")
                .limiteCredito(new BigDecimal("1000000.00"))
                .saldoCredito(new BigDecimal("0.00"))
                .build();
        producto = Producto.builder()
                .id(10L).codigo("P-001").nombre("Bujia")
                .precioVenta(new BigDecimal("100000.00"))
                .costoAdquisicion(new BigDecimal("60000.00"))
                .stockActual(10).stockMinimo(2)
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(ventaRepository.siguienteNumero()).thenReturn(1L);
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));
        when(facturaRepository.findByVentaId(any())).thenReturn(Optional.empty());
    }

    private VentaRequest pedido(TipoPago tipo, Integer cuotas, int cantidad) {
        return new VentaRequest(1L, tipo, cuotas, List.of(new VentaRequest.Linea(10L, cantidad)));
    }

    // CP-01
    @Test
    void ventaDeContadoSeConfirmaDescuentaStockYEmiteFactura() {
        VentaResponse r = service.registrar(pedido(TipoPago.CONTADO, null, 2));

        assertEquals(EstadoVenta.CONFIRMADA, r.estado());
        assertEquals(new BigDecimal("200000.00"), r.subtotal());
        assertEquals(new BigDecimal("38000.00"), r.iva());
        assertEquals(new BigDecimal("238000.00"), r.total());
        verify(inventarioService).registrarSalida(eq(producto), eq(2), contains("Venta VIT-"), any(Venta.class));
        verify(facturaRepository).save(any(Factura.class));
        verify(creditoRepository, never()).save(any(Credito.class));
    }

    // CP-02
    @Test
    void ventaACreditoDentroDelCupoQuedaPendienteYNoDescuentaStock() {
        VentaResponse r = service.registrar(pedido(TipoPago.CREDITO, 3, 2));

        assertEquals(EstadoVenta.PENDIENTE_AUTORIZACION, r.estado());
        verify(creditoRepository).save(any(Credito.class));
        verify(inventarioService, never()).registrarSalida(any(), anyInt(), anyString(), any());
        verify(facturaRepository, never()).save(any(Factura.class));
    }

    // CP-03
    @Test
    void ventaACreditoQueExcedeElCupoSeRechazaConMensaje() {
        cliente.setLimiteCredito(new BigDecimal("100000.00"));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> service.registrar(pedido(TipoPago.CREDITO, 3, 2)));

        assertTrue(ex.getMessage().contains("El total de la venta (238000.00) excede el cupo de credito disponible del cliente (100000.00)"),
                ex.getMessage());
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    // CP-03 (cupo ya agotado: el disponible mostrado nunca es negativo)
    @Test
    void conSaldoMayorAlLimiteElCupoMostradoEsCero() {
        cliente.setLimiteCredito(new BigDecimal("100000.00"));
        cliente.setSaldoCredito(new BigDecimal("150000.00"));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> service.registrar(pedido(TipoPago.CREDITO, 3, 2)));

        assertTrue(ex.getMessage().endsWith("del cliente (0)"), ex.getMessage());
    }

    // CP-04
    @Test
    void ventaACreditoConTotalIgualAlCupoSeAcepta() {
        cliente.setLimiteCredito(new BigDecimal("238000.00"));

        VentaResponse r = service.registrar(pedido(TipoPago.CREDITO, 3, 2));

        assertEquals(EstadoVenta.PENDIENTE_AUTORIZACION, r.estado());
    }

    // CP-12 (H-06): el total no se puede dividir en las cuotas pedidas -> se rechaza al registrar
    @Test
    void ventaACreditoQueNoSePuedeDividirEnLasCuotasPedidasSeRechazaAlRegistrar() {
        producto.setPrecioVenta(new BigDecimal("0.01"));   // total = 0.02; en 36 cuotas la ultima seria negativa

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> service.registrar(pedido(TipoPago.CREDITO, 36, 1)));

        assertEquals("El valor no se puede dividir en 36 cuotas", ex.getMessage());
        verify(ventaRepository, never()).save(any(Venta.class));
        verify(creditoRepository, never()).save(any(Credito.class));
    }

    // CP-13 (H-06): un total normal en el maximo de cuotas (36) se sigue aceptando
    @Test
    void ventaACreditoEnElMaximoDeCuotasSeAcepta() {
        VentaResponse r = service.registrar(pedido(TipoPago.CREDITO, 36, 2));

        assertEquals(EstadoVenta.PENDIENTE_AUTORIZACION, r.estado());
        verify(creditoRepository).save(any(Credito.class));
    }

    // CP-10
    @Test
    void ventaConCantidadMayorAlStockSeRechaza() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> service.registrar(pedido(TipoPago.CONTADO, null, 11)));

        assertEquals("Stock insuficiente para 'Bujia': disponible 10, solicitado 11", ex.getMessage());
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    void ventaACreditoSinNumeroDeCuotasSeRechaza() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> service.registrar(pedido(TipoPago.CREDITO, null, 1)));

        assertEquals("Una venta a credito requiere el numero de cuotas", ex.getMessage());
    }

    // ---- cancelar ----

    private Venta ventaConfirmadaACredito() {
        Venta v = new Venta();
        v.setId(7L);
        v.setNumero("VIT-2026-00001");
        v.setCliente(cliente);
        v.setTipoPago(TipoPago.CREDITO);
        v.setEstado(EstadoVenta.CONFIRMADA);
        v.setSubtotal(new BigDecimal("200000.00"));
        v.setIva(new BigDecimal("38000.00"));
        v.setTotal(new BigDecimal("238000.00"));
        DetalleVenta d = new DetalleVenta();
        d.setVenta(v);
        d.setProducto(producto);
        d.setCantidad(2);
        d.setPrecioUnitario(new BigDecimal("100000.00"));
        d.setCostoUnitario(new BigDecimal("60000.00"));
        d.setSubtotal(new BigDecimal("200000.00"));
        v.getDetalles().add(d);
        when(ventaRepository.findById(7L)).thenReturn(Optional.of(v));
        return v;
    }

    private Credito creditoDe(Venta v) {
        Credito c = new Credito();
        c.setVenta(v);
        c.setCliente(cliente);
        c.setValorTotal(new BigDecimal("238000.00"));
        c.setNumeroCuotas(3);
        c.setEstado(EstadoCredito.APROBADO);
        when(creditoRepository.findByVentaId(7L)).thenReturn(Optional.of(c));
        return c;
    }

    // CP-08
    @Test
    void cancelarVentaACreditoSinPagosBajaElSaldoCancelaElCreditoYReponeElStock() {
        cliente.setSaldoCredito(new BigDecimal("500000.00"));
        Venta v = ventaConfirmadaACredito();
        Credito credito = creditoDe(v);

        VentaResponse r = service.cancelar(7L, "Cliente desistio");

        assertEquals(EstadoVenta.CANCELADA, r.estado());
        assertEquals(new BigDecimal("262000.00"), cliente.getSaldoCredito());
        assertEquals(EstadoCredito.CANCELADO, credito.getEstado());
        assertEquals("Venta cancelada", credito.getObservacion());
        verify(inventarioService).registrarEntrada(eq(producto), eq(2),
                contains("Cancelacion de la venta VIT-2026-00001: Cliente desistio"), isNull());
    }

    // CP-08 (el saldo nunca queda negativo)
    @Test
    void cancelarNuncaDejaElSaldoNegativo() {
        cliente.setSaldoCredito(new BigDecimal("100000.00"));
        Venta v = ventaConfirmadaACredito();
        creditoDe(v);

        service.cancelar(7L, null);

        assertEquals(new BigDecimal("0.00"), cliente.getSaldoCredito());
    }

    // CP-09
    @Test
    void cancelarConCuotasPagadasSeRechazaYNoCambiaNada() {
        cliente.setSaldoCredito(new BigDecimal("500000.00"));
        Venta v = ventaConfirmadaACredito();
        Credito credito = creditoDe(v);
        Cuota pagada = new Cuota();
        pagada.setEstado(EstadoCuota.PAGADA);
        credito.getCuotas().add(pagada);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.cancelar(7L, null));

        assertEquals("No se puede cancelar: el credito ya tiene cuotas pagadas", ex.getMessage());
        assertEquals(new BigDecimal("500000.00"), cliente.getSaldoCredito());
        assertEquals(EstadoVenta.CONFIRMADA, v.getEstado());
        verify(inventarioService, never()).registrarEntrada(any(), anyInt(), anyString(), any());
    }

    @Test
    void soloSePuedenCancelarVentasConfirmadas() {
        Venta v = ventaConfirmadaACredito();
        v.setEstado(EstadoVenta.PENDIENTE_AUTORIZACION);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.cancelar(7L, null));

        assertTrue(ex.getMessage().startsWith("Solo se pueden cancelar ventas confirmadas"), ex.getMessage());
    }
}