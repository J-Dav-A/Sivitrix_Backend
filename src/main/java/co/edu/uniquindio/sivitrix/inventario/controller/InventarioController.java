package co.edu.uniquindio.sivitrix.inventario.controller;

import co.edu.uniquindio.sivitrix.inventario.dto.MovimientoRequest;
import co.edu.uniquindio.sivitrix.inventario.dto.MovimientoResponse;
import co.edu.uniquindio.sivitrix.inventario.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** F-02: kardex y movimientos manuales de inventario. */
@RestController
@RequestMapping("/api/inventario/movimientos")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    public List<MovimientoResponse> listar(@RequestParam(required = false) Long productoId) {
        return inventarioService.listar(productoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovimientoResponse registrar(@Valid @RequestBody MovimientoRequest request) {
        return inventarioService.registrarMovimiento(request);
    }
}
