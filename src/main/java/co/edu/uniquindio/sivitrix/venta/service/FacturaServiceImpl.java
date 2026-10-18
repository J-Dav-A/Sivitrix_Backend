package co.edu.uniquindio.sivitrix.venta.service;

import co.edu.uniquindio.sivitrix.cliente.entity.ClienteEmpresarial;
import co.edu.uniquindio.sivitrix.common.exception.ResourceNotFoundException;
import co.edu.uniquindio.sivitrix.venta.dto.FacturaResponse;
import co.edu.uniquindio.sivitrix.venta.entity.Factura;
import co.edu.uniquindio.sivitrix.venta.entity.Venta;
import co.edu.uniquindio.sivitrix.venta.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FacturaServiceImpl implements FacturaService {

    private final FacturaRepository facturaRepository;

    @Override
    public List<FacturaResponse> listar() {
        return facturaRepository.findAllByOrderByFechaEmisionDesc().stream().map(this::toResponse).toList();
    }

    @Override
    public FacturaResponse obtenerPorId(Long id) {
        return toResponse(facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro la factura con id " + id)));
    }

    @Override
    public FacturaResponse obtenerPorVenta(Long ventaId) {
        return toResponse(facturaRepository.findByVentaId(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La venta " + ventaId + " no tiene factura (solo se factura al confirmarse)")));
    }

    private FacturaResponse toResponse(Factura f) {
        Venta v = f.getVenta();
        ClienteEmpresarial c = v.getCliente();
        List<FacturaResponse.Linea> lineas = v.getDetalles().stream()
                .map(d -> new FacturaResponse.Linea(
                        d.getProducto().getCodigo(),
                        d.getProducto().getNombre(),
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                        d.getSubtotal()))
                .toList();
        return new FacturaResponse(
                f.getId(), f.getNumero(), v.getId(), f.getFechaEmision(), v.getTipoPago(),
                new FacturaResponse.Cliente(c.getNit(), c.getNombre(), c.getDireccion(), c.getTelefono(), c.getEmail()),
                lineas, f.getSubtotal(), f.getIva(), f.getTotal());
    }
}
