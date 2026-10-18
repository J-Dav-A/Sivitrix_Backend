package co.edu.uniquindio.sivitrix.pedido.service;

import co.edu.uniquindio.sivitrix.pedido.dto.PedidoRequest;
import co.edu.uniquindio.sivitrix.pedido.dto.PedidoResponse;
import co.edu.uniquindio.sivitrix.pedido.dto.RecepcionRequest;

import java.util.List;

public interface PedidoService {

    PedidoResponse crear(PedidoRequest request);

    List<PedidoResponse> listar(Long proveedorId);

    PedidoResponse obtenerPorId(Long id);

    PedidoResponse registrarRecepcion(Long id, RecepcionRequest request);

    PedidoResponse cancelar(Long id);
}
