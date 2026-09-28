package co.edu.uniquindio.sivitrix.producto;

import co.edu.uniquindio.sivitrix.producto.dto.ProductoRequest;
import co.edu.uniquindio.sivitrix.producto.dto.ProductoResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-27T21:48:29-0500",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class ProductoMapperImpl implements ProductoMapper {

    @Override
    public Producto toEntity(ProductoRequest request) {
        if ( request == null ) {
            return null;
        }

        Producto.ProductoBuilder producto = Producto.builder();

        producto.codigo( request.codigo() );
        producto.nombre( request.nombre() );
        producto.categoria( request.categoria() );
        producto.precioVenta( request.precioVenta() );
        producto.costoAdquisicion( request.costoAdquisicion() );
        producto.stockMinimo( request.stockMinimo() );

        return producto.build();
    }

    @Override
    public ProductoResponse toResponse(Producto producto) {
        if ( producto == null ) {
            return null;
        }

        Long id = null;
        String codigo = null;
        String nombre = null;
        CategoriaProducto categoria = null;
        BigDecimal precioVenta = null;
        BigDecimal costoAdquisicion = null;
        Integer stockActual = null;
        Integer stockMinimo = null;
        LocalDateTime creadoEn = null;
        LocalDateTime actualizadoEn = null;

        id = producto.getId();
        codigo = producto.getCodigo();
        nombre = producto.getNombre();
        categoria = producto.getCategoria();
        precioVenta = producto.getPrecioVenta();
        costoAdquisicion = producto.getCostoAdquisicion();
        stockActual = producto.getStockActual();
        stockMinimo = producto.getStockMinimo();
        creadoEn = producto.getCreadoEn();
        actualizadoEn = producto.getActualizadoEn();

        BigDecimal margenValor = calcularMargenValor(producto);
        BigDecimal margenPorcentaje = calcularMargenPorcentaje(producto);
        Integer diferenciaConMinimo = producto.getStockActual() - producto.getStockMinimo();

        ProductoResponse productoResponse = new ProductoResponse( id, codigo, nombre, categoria, precioVenta, costoAdquisicion, margenValor, margenPorcentaje, stockActual, stockMinimo, diferenciaConMinimo, creadoEn, actualizadoEn );

        return productoResponse;
    }
}
