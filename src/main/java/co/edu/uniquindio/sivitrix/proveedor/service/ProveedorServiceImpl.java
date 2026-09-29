package co.edu.uniquindio.sivitrix.proveedor.service;

import co.edu.uniquindio.sivitrix.common.exception.DuplicateResourceException;
import co.edu.uniquindio.sivitrix.common.exception.ResourceNotFoundException;
import co.edu.uniquindio.sivitrix.producto.entity.Producto;
import co.edu.uniquindio.sivitrix.producto.repository.ProductoRepository;
import co.edu.uniquindio.sivitrix.proveedor.dto.ProveedorRequest;
import co.edu.uniquindio.sivitrix.proveedor.dto.ProveedorResponse;
import co.edu.uniquindio.sivitrix.proveedor.entity.Proveedor;
import co.edu.uniquindio.sivitrix.proveedor.mapper.ProveedorMapper;
import co.edu.uniquindio.sivitrix.proveedor.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final ProveedorMapper proveedorMapper;

    @Override
    public ProveedorResponse crear(ProveedorRequest request) {
        // SWR-32: el NIT debe ser unico.
        if (proveedorRepository.existsByNit(request.nit())) {
            throw new DuplicateResourceException(
                    "Ya existe un proveedor registrado con el NIT '" + request.nit() + "'");
        }

        Proveedor proveedor = proveedorMapper.toEntity(request);
        proveedor.setProductos(buscarProductos(request.productosIds()));

        Proveedor guardado = proveedorRepository.save(proveedor);
        return proveedorMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorResponse> listar() {
        return proveedorRepository.findAll().stream()
                .map(proveedorMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponse obtenerPorId(Long id) {
        return proveedorMapper.toResponse(buscarOFallar(id));
    }

    @Override
    public ProveedorResponse actualizar(Long id, ProveedorRequest request) {
        Proveedor proveedor = buscarOFallar(id);

        // El NIT no se modifica en una actualizacion (mismo criterio que
        // usamos con Producto y Cliente).
        proveedor.setNombreEmpresa(request.nombreEmpresa());
        proveedor.setNombreContacto(request.nombreContacto());
        proveedor.setTelefono(request.telefono());
        proveedor.setEmail(request.email());
        proveedor.setProductos(buscarProductos(request.productosIds()));

        return proveedorMapper.toResponse(proveedorRepository.save(proveedor));
    }

    @Override
    public void eliminar(Long id) {
        // TODO (SWR-14): cuando exista el modulo de Pedidos, verificar aqui
        // que el proveedor no tenga pedidos activos antes de eliminarlo.
        Proveedor proveedor = buscarOFallar(id);
        proveedorRepository.delete(proveedor);
    }

    private Set<Producto> buscarProductos(Set<Long> productosIds) {
        if (productosIds == null || productosIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(productoRepository.findAllById(productosIds));
    }

    private Proveedor buscarOFallar(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro el proveedor con id " + id));
    }
}