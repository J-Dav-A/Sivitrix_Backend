package co.edu.uniquindio.sivitrix.producto.service;

import co.edu.uniquindio.sivitrix.common.exception.DuplicateResourceException;
import co.edu.uniquindio.sivitrix.common.exception.ResourceNotFoundException;
import co.edu.uniquindio.sivitrix.producto.entity.CategoriaProducto;
import co.edu.uniquindio.sivitrix.producto.entity.Producto;
import co.edu.uniquindio.sivitrix.producto.mapper.ProductoMapper;
import co.edu.uniquindio.sivitrix.producto.repository.ProductoRepository;
import co.edu.uniquindio.sivitrix.producto.dto.ProductoRequest;
import co.edu.uniquindio.sivitrix.producto.dto.ProductoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Override
    public ProductoResponse crear(ProductoRequest request) {
        // SWR-27: el codigo es unico y no se puede modificar despues.
        if (productoRepository.existsByCodigo(request.codigo())) {
            throw new DuplicateResourceException(
                    "Ya existe un producto registrado con el codigo '" + request.codigo() + "'");
        }

        Producto producto = productoMapper.toEntity(request);
        producto.setStockActual(0); // un producto nuevo inicia sin existencias

        Producto guardado = productoRepository.save(producto);
        return productoMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listar(String nombre, CategoriaProducto categoria) {
        // SWR-12: consulta filtrada por nombre o categoria.
        List<Producto> productos;
        if (nombre != null && !nombre.isBlank()) {
            productos = productoRepository.findByNombreContainingIgnoreCase(nombre);
        } else if (categoria != null) {
            productos = productoRepository.findByCategoria(categoria);
        } else {
            productos = productoRepository.findAll();
        }
        return productos.stream().map(productoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorId(Long id) {
        return productoMapper.toResponse(buscarOFallar(id));
    }

    @Override
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = buscarOFallar(id);

        // SWR-27: el codigo no se puede modificar una vez creado.
        producto.setNombre(request.nombre());
        producto.setCategoria(request.categoria());
        producto.setPrecioVenta(request.precioVenta());
        producto.setCostoAdquisicion(request.costoAdquisicion());
        producto.setStockMinimo(request.stockMinimo());

        return productoMapper.toResponse(productoRepository.save(producto));
    }

    @Override
    public void eliminar(Long id) {
        Producto producto = buscarOFallar(id);
        productoRepository.delete(producto);
    }

    private Producto buscarOFallar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro el producto con id " + id));
    }
}
