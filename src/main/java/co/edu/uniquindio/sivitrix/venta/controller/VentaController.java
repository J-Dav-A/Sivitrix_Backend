package co.edu.uniquindio.sivitrix.venta.controller;

import co.edu.uniquindio.sivitrix.venta.dto.FacturaResponse;
import co.edu.uniquindio.sivitrix.venta.dto.VentaRequest;
import co.edu.uniquindio.sivitrix.venta.dto.VentaResponse;
import co.edu.uniquindio.sivitrix.venta.entity.EstadoVenta;
import co.edu.uniquindio.sivitrix.venta.service.FacturaService;
import co.edu.uniquindio.sivitrix.venta.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/** F-01: registro y consulta de ventas. */
@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;
    private final FacturaService facturaService;

    @PostMapping
    public ResponseEntity<VentaResponse> registrar(@Valid @RequestBody VentaRequest request) {
        VentaResponse creada = ventaService.registrar(request);
        return ResponseEntity.created(URI.create("/api/ventas/" + creada.id())).body(creada);
    }

    @GetMapping
    public List<VentaResponse> listar(@RequestParam(required = false) EstadoVenta estado,
                                      @RequestParam(required = false) Long clienteId) {
        return ventaService.listar(estado, clienteId);
    }

    @GetMapping("/{id}")
    public VentaResponse obtener(@PathVariable Long id) {
        return ventaService.obtenerPorId(id);
    }

    @PostMapping("/{id}/cancelar")
    public VentaResponse cancelar(@PathVariable Long id,
                                  @RequestBody(required = false) java.util.Map<String, String> cuerpo) {
        return ventaService.cancelar(id, cuerpo == null ? null : cuerpo.get("motivo"));
    }

    @GetMapping("/{id}/factura")
    public FacturaResponse factura(@PathVariable Long id) {
        return facturaService.obtenerPorVenta(id);
    }
}
