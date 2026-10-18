package co.edu.uniquindio.sivitrix.inventario.service;

import co.edu.uniquindio.sivitrix.inventario.dto.MovimientoRequest;
import co.edu.uniquindio.sivitrix.inventario.dto.MovimientoResponse;
import co.edu.uniquindio.sivitrix.inventario.entity.MovimientoInventario;
import co.edu.uniquindio.sivitrix.pedido.entity.Pedido;
import co.edu.uniquindio.sivitrix.producto.entity.Producto;
import co.edu.uniquindio.sivitrix.venta.entity.Venta;

import java.util.List;

public interface InventarioService {

    MovimientoResponse registrarMovimiento(MovimientoRequest request);

    List<MovimientoResponse> listar(Long productoId);

    // Operaciones internas que usan los modulos de Pedidos y Ventas.
    MovimientoInventario registrarEntrada(Producto producto, int cantidad, String observacion, Pedido pedido);

    MovimientoInventario registrarSalida(Producto producto, int cantidad, String observacion, Venta venta);
}