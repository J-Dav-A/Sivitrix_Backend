package co.edu.uniquindio.sivitrix.pedido.dto;

import co.edu.uniquindio.sivitrix.pedido.entity.EstadoPedido;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long proveedorId,
        String proveedorNombre,
        LocalDateTime fechaPedido,
        EstadoPedido estado,
        String observaciones,
        List<Detalle> detalles
) {
    public record Detalle(
            Long id,
            Long productoId,
            String productoCodigo,
            String productoNombre,
            Integer cantidadSolicitada,
            Integer cantidadRecibida,
            Integer pendiente
    ) {
    }
}