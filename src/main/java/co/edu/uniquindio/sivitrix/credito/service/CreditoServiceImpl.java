package co.edu.uniquindio.sivitrix.credito.service;

import co.edu.uniquindio.sivitrix.cliente.entity.ClienteEmpresarial;
import co.edu.uniquindio.sivitrix.cliente.repository.ClienteEmpresarialRepository;
import co.edu.uniquindio.sivitrix.common.exception.BusinessRuleException;
import co.edu.uniquindio.sivitrix.common.exception.ResourceNotFoundException;
import co.edu.uniquindio.sivitrix.common.util.FinanzasUtils;
import co.edu.uniquindio.sivitrix.common.util.MoneyUtils;
import co.edu.uniquindio.sivitrix.credito.dto.CreditoResponse;
import co.edu.uniquindio.sivitrix.credito.entity.*;
import co.edu.uniquindio.sivitrix.credito.repository.CreditoRepository;
import co.edu.uniquindio.sivitrix.credito.repository.CuotaRepository;
import co.edu.uniquindio.sivitrix.venta.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CreditoServiceImpl implements CreditoService {

    // Plazo entre cuotas (dias).
    private static final int DIAS_ENTRE_CUOTAS = 30;

    private final CreditoRepository creditoRepository;
    private final CuotaRepository cuotaRepository;
    private final ClienteEmpresarialRepository clienteRepository;
    private final VentaService ventaService;

    @Override
    @Transactional(readOnly = true)
    public List<CreditoResponse> listar(EstadoCredito estado, Long clienteId) {
        return creditoRepository.findAllByOrderByFechaSolicitudDesc().stream()
                .filter(c -> estado == null || c.getEstado() == estado)
                .filter(c -> clienteId == null || c.getCliente().getId().equals(clienteId))
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CreditoResponse obtenerPorId(Long id) {
        return toResponse(buscarOFallar(id));
    }

    @Override
    public CreditoResponse autorizar(Long id) {
        Credito credito = buscarOFallar(id);
        exigirPendiente(credito);

        ClienteEmpresarial cliente = credito.getCliente();
        if (!cliente.puedeFinanciar(credito.getValorTotal())) {
            throw new BusinessRuleException("No se puede autorizar: el credito (" + credito.getValorTotal()
                    + ") excede el cupo disponible del cliente (" + cliente.cupoDisponible() + ")");
        }

        generarCuotas(credito);

        credito.setEstado(EstadoCredito.APROBADO);
        credito.setFechaResolucion(LocalDateTime.now());

        // SWR-37: el saldo de credito del cliente sube con cada credito aprobado.
        cliente.aumentarSaldo(credito.getValorTotal());
        clienteRepository.save(cliente);

        // La venta se confirma: descuenta stock y emite la factura.
        ventaService.confirmarTrasAutorizacion(credito.getVenta().getId());

        return toResponse(creditoRepository.save(credito));
    }

    @Override
    public CreditoResponse rechazar(Long id, String motivo) {
        Credito credito = buscarOFallar(id);
        exigirPendiente(credito);

        credito.setEstado(EstadoCredito.RECHAZADO);
        credito.setFechaResolucion(LocalDateTime.now());
        credito.setObservacion((motivo == null || motivo.isBlank()) ? "Rechazado por el administrador" : motivo);
        ventaService.rechazarTrasAutorizacion(credito.getVenta().getId());

        return toResponse(creditoRepository.save(credito));
    }

    @Override
    public CreditoResponse pagarCuota(Long cuotaId) {
        Cuota cuota = cuotaRepository.findById(cuotaId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro la cuota con id " + cuotaId));
        Credito credito = cuota.getCredito();

        if (credito.getEstado() != EstadoCredito.APROBADO) {
            throw new BusinessRuleException("Solo se pueden pagar cuotas de un credito aprobado");
        }
        if (cuota.getEstado() == EstadoCuota.PAGADA) {
            throw new BusinessRuleException("La cuota " + cuota.getNumero() + " ya esta pagada");
        }

        Pago pago = new Pago();
        pago.setCuota(cuota);
        pago.setMonto(cuota.getValor());
        pago.setFechaPago(LocalDateTime.now());
        cuota.setPago(pago);
        cuota.setEstado(EstadoCuota.PAGADA);
        cuotaRepository.save(cuota);

        // Baja el saldo de credito del cliente.
        ClienteEmpresarial cliente = credito.getCliente();
        cliente.reducirSaldo(cuota.getValor());
        clienteRepository.save(cliente);

        boolean todasPagadas = credito.getCuotas().stream().allMatch(c -> c.getEstado() == EstadoCuota.PAGADA);
        if (todasPagadas) {
            credito.setEstado(EstadoCredito.PAGADO);
            creditoRepository.save(credito);
        }
        return toResponse(credito);
    }

    // Cuotas iguales redondeadas hacia arriba; la ultima absorbe la diferencia (ver FinanzasUtils).
    private void generarCuotas(Credito credito) {
        int n = credito.getNumeroCuotas();
        List<BigDecimal> valores;
        try {
            valores = FinanzasUtils.dividirEnCuotas(credito.getValorTotal(), n);
        } catch (IllegalArgumentException ex) {
            throw new BusinessRuleException(ex.getMessage());
        }

        LocalDate hoy = LocalDate.now();
        for (int i = 1; i <= n; i++) {
            Cuota cuota = new Cuota();
            cuota.setCredito(credito);
            cuota.setNumero(i);
            cuota.setValor(valores.get(i - 1));
            cuota.setFechaLimite(hoy.plusDays((long) DIAS_ENTRE_CUOTAS * i));
            cuota.setEstado(EstadoCuota.PENDIENTE);
            credito.getCuotas().add(cuota);
        }
    }

    private void exigirPendiente(Credito credito) {
        if (credito.getEstado() != EstadoCredito.PENDIENTE_AUTORIZACION) {
            throw new BusinessRuleException("El credito #" + credito.getId() + " ya fue resuelto (estado "
                    + credito.getEstado() + ")");
        }
    }

    private Credito buscarOFallar(Long id) {
        return creditoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el credito con id " + id));
    }

    private CreditoResponse toResponse(Credito c) {
        LocalDate hoy = LocalDate.now();
        List<CreditoResponse.CuotaResponse> cuotas = c.getCuotas().stream()
                .map(q -> new CreditoResponse.CuotaResponse(
                        q.getId(),
                        q.getNumero(),
                        q.getValor(),
                        q.getFechaLimite(),
                        // Una cuota pendiente cuya fecha ya paso se muestra como vencida.
                        (q.getEstado() == EstadoCuota.PENDIENTE && q.getFechaLimite().isBefore(hoy))
                                ? EstadoCuota.VENCIDA : q.getEstado(),
                        q.getPago() != null ? q.getPago().getFechaPago() : null))
                .toList();

        BigDecimal saldoPendiente = c.getCuotas().stream()
                .filter(q -> c.getEstado() != EstadoCredito.CANCELADO && q.getEstado() != EstadoCuota.PAGADA)                .map(Cuota::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CreditoResponse(
                c.getId(),
                c.getVenta().getId(),
                c.getVenta().getNumero(),
                c.getCliente().getId(),
                c.getCliente().getNit(),
                c.getCliente().getNombre(),
                c.getValorTotal(),
                c.getNumeroCuotas(),
                c.getEstado(),
                c.getFechaSolicitud(),
                c.getFechaResolucion(),
                c.getObservacion(),
                MoneyUtils.redondear(saldoPendiente),
                cuotas);
    }
}