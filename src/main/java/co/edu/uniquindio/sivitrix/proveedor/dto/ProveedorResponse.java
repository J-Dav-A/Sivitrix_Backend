package co.edu.uniquindio.sivitrix.proveedor.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record ProveedorResponse(
        Long id,
        String nit,
        String nombreEmpresa,
        String nombreContacto,
        String telefono,
        String email,
        Set<ProductoResumen> productos,
        LocalDateTime fechaUltimoPedido,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn
) {
    // Version reducida del producto: aqui no necesitamos precio, costo
    // ni margen (eso es informacion del modulo de Inventario, no del de
    // Proveedores). Evita acoplar este DTO con ProductoResponse.
    public record ProductoResumen(Long id, String codigo, String nombre) {
    }
}