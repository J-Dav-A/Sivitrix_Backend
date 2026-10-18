package co.edu.uniquindio.sivitrix.venta.service;

import co.edu.uniquindio.sivitrix.venta.dto.FacturaResponse;

import java.util.List;

public interface FacturaService {

    List<FacturaResponse> listar();

    FacturaResponse obtenerPorId(Long id);

    FacturaResponse obtenerPorVenta(Long ventaId);
}
