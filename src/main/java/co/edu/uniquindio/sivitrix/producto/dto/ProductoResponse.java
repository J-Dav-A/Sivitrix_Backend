package co.edu.uniquindio.sivitrix.producto.dto;

import co.edu.uniquindio.sivitrix.producto.CategoriaProducto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SWR-25: incluye el margen de ganancia (valor y porcentaje) ya calculado.
 * SWR-26: por ahora este DTO siempre incluye costo/margen; cuando se active
 * Spring Security, el controlador debera devolver una variante sin estos
 * campos para el rol Vendedor (ver comentario en ProductoController).
 * SWR-28: incluye la diferencia entre stock actual y stock minimo.
 */
public record ProductoResponse(
        Long id,
        String codigo,
        String nombre,
        CategoriaProducto categoria,
        BigDecimal precioVenta,
        BigDecimal costoAdquisicion,
        BigDecimal margenValor,
        BigDecimal margenPorcentaje,
        Integer stockActual,
        Integer stockMinimo,
        Integer diferenciaConMinimo,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn
) {
}
