package co.edu.uniquindio.sivitrix.producto.controller;

import co.edu.uniquindio.sivitrix.producto.entity.CategoriaProducto;
import co.edu.uniquindio.sivitrix.producto.service.ProductoService;
import co.edu.uniquindio.sivitrix.producto.dto.ProductoRequest;
import co.edu.uniquindio.sivitrix.producto.dto.ProductoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import co.edu.uniquindio.sivitrix.seguridad.ContextoSeguridad;
import co.edu.uniquindio.sivitrix.producto.entity.EstadoStock;

import java.net.URI;
import java.util.List;

/**
 * SWR-08, SWR-12, SWR-27: alta y consulta de productos del inventario.
 *
 * NOTA (SWR-26): cuando se active Spring Security, el rol Vendedor debe
 * recibir una respuesta sin costoAdquisicion/margenValor/margenPorcentaje.
 * Por ahora todos los campos son visibles para cualquier cliente de la API.
 */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final ContextoSeguridad contexto;

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse creado = productoService.crear(request);
        return ResponseEntity.created(URI.create("/api/productos/" + creado.id())).body(creado);
    }

    @GetMapping
    public List<ProductoResponse> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) CategoriaProducto categoria,
            @RequestParam(required = false) EstadoStock estadoStock) {
        return productoService.listar(nombre, categoria, estadoStock).stream().map(this::filtrar).toList();
    }

    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable Long id) {
        return filtrar(productoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return productoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }

    // SWR-26: oculta costo y margen a quien no sea administrador.
    private ProductoResponse filtrar(ProductoResponse p) {
        if (contexto.esAdministrador()) {
            return p;
        }
        return new ProductoResponse(p.id(), p.codigo(), p.nombre(), p.categoria(), p.precioVenta(),
                null, null, null, p.stockActual(), p.stockMinimo(), p.diferenciaConMinimo(),
                p.creadoEn(), p.actualizadoEn());
    }

}
