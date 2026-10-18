package co.edu.uniquindio.sivitrix.producto.service;

import co.edu.uniquindio.sivitrix.producto.entity.CategoriaProducto;
import co.edu.uniquindio.sivitrix.producto.dto.ProductoRequest;
import co.edu.uniquindio.sivitrix.producto.dto.ProductoResponse;
import co.edu.uniquindio.sivitrix.producto.entity.EstadoStock;

import java.util.List;

public interface ProductoService {

    ProductoResponse crear(ProductoRequest request);

    List<ProductoResponse> listar(String nombre, CategoriaProducto categoria, EstadoStock estadoStock);
    ProductoResponse obtenerPorId(Long id);

    ProductoResponse actualizar(Long id, ProductoRequest request);

    void eliminar(Long id);
}
