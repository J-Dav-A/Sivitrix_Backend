package co.edu.uniquindio.sivitrix.pedido.controller;

import co.edu.uniquindio.sivitrix.pedido.dto.PedidoRequest;
import co.edu.uniquindio.sivitrix.pedido.dto.PedidoResponse;
import co.edu.uniquindio.sivitrix.pedido.dto.RecepcionRequest;
import co.edu.uniquindio.sivitrix.pedido.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/** F-03: pedidos a proveedores y recepcion de mercancia. */
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody PedidoRequest request) {
        PedidoResponse creado = pedidoService.crear(request);
        return ResponseEntity.created(URI.create("/api/pedidos/" + creado.id())).body(creado);
    }

    @GetMapping
    public List<PedidoResponse> listar(@RequestParam(required = false) Long proveedorId) {
        return pedidoService.listar(proveedorId);
    }

    @GetMapping("/{id}")
    public PedidoResponse obtener(@PathVariable Long id) {
        return pedidoService.obtenerPorId(id);
    }

    @PostMapping("/{id}/recepcion")
    public PedidoResponse registrarRecepcion(@PathVariable Long id, @Valid @RequestBody RecepcionRequest request) {
        return pedidoService.registrarRecepcion(id, request);
    }

    @PostMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable Long id) {
        return pedidoService.cancelar(id);
    }
}