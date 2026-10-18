package co.edu.uniquindio.sivitrix.venta.service;

import co.edu.uniquindio.sivitrix.venta.dto.VentaRequest;
import co.edu.uniquindio.sivitrix.venta.dto.VentaResponse;
import co.edu.uniquindio.sivitrix.venta.entity.EstadoVenta;

import java.util.List;

public interface VentaService {

    VentaResponse registrar(VentaRequest request);

    List<VentaResponse> listar(EstadoVenta estado, Long clienteId);

    VentaResponse obtenerPorId(Long id);

    // Invocados por el modulo de Credito al resolver la autorizacion.
    void confirmarTrasAutorizacion(Long ventaId);

    void rechazarTrasAutorizacion(Long ventaId);
}
