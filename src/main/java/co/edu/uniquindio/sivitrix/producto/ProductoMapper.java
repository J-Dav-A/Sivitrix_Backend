package co.edu.uniquindio.sivitrix.producto;

import co.edu.uniquindio.sivitrix.common.util.MoneyUtils;
import co.edu.uniquindio.sivitrix.producto.dto.ProductoRequest;
import co.edu.uniquindio.sivitrix.producto.dto.ProductoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stockActual", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "actualizadoEn", ignore = true)
    Producto toEntity(ProductoRequest request);

    @Mapping(target = "margenValor", expression = "java(calcularMargenValor(producto))")
    @Mapping(target = "margenPorcentaje", expression = "java(calcularMargenPorcentaje(producto))")
    @Mapping(target = "diferenciaConMinimo",
             expression = "java(producto.getStockActual() - producto.getStockMinimo())")
    ProductoResponse toResponse(Producto producto);

    // SWR-25: margen = precioVenta - costoAdquisicion, redondeado (SWR-20).
    default BigDecimal calcularMargenValor(Producto producto) {
        BigDecimal margen = producto.getPrecioVenta().subtract(producto.getCostoAdquisicion());
        return MoneyUtils.redondear(margen);
    }

    // SWR-25: margen en porcentaje respecto al costo de adquisicion.
    default BigDecimal calcularMargenPorcentaje(Producto producto) {
        BigDecimal costo = producto.getCostoAdquisicion();
        if (costo == null || costo.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.CEILING);
        }
        BigDecimal margenValor = calcularMargenValor(producto);
        BigDecimal porcentaje = margenValor
                .divide(costo, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        return MoneyUtils.redondear(porcentaje);
    }
}
