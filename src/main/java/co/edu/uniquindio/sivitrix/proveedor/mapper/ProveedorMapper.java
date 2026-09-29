package co.edu.uniquindio.sivitrix.proveedor.mapper;

import co.edu.uniquindio.sivitrix.producto.entity.Producto;
import co.edu.uniquindio.sivitrix.proveedor.dto.ProveedorRequest;
import co.edu.uniquindio.sivitrix.proveedor.dto.ProveedorResponse;
import co.edu.uniquindio.sivitrix.proveedor.dto.ProveedorResponse.ProductoResumen;
import co.edu.uniquindio.sivitrix.proveedor.entity.Proveedor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProveedorMapper {

    // "productos" se ignora aqui a proposito: buscar los Producto reales
    // a partir de productosIds requiere consultar la base de datos, y un
    // mapper no debe hacer consultas. Eso lo hace ProveedorServiceImpl.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productos", ignore = true)
    @Mapping(target = "fechaUltimoPedido", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "actualizadoEn", ignore = true)
    Proveedor toEntity(ProveedorRequest request);

    @Mapping(target = "productos", expression = "java(mapearProductos(proveedor.getProductos()))")
    ProveedorResponse toResponse(Proveedor proveedor);

    default Set<ProductoResumen> mapearProductos(Set<Producto> productos) {
        return productos.stream()
                .map(p -> new ProductoResumen(p.getId(), p.getCodigo(), p.getNombre()))
                .collect(Collectors.toSet());
    }
}