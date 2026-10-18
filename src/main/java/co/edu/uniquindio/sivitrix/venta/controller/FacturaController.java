package co.edu.uniquindio.sivitrix.venta.controller;

import co.edu.uniquindio.sivitrix.venta.dto.FacturaResponse;
import co.edu.uniquindio.sivitrix.venta.service.FacturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** F-06: consulta de facturas emitidas. */
@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaService facturaService;

    @GetMapping
    public List<FacturaResponse> listar() {
        return facturaService.listar();
    }

    @GetMapping("/{id}")
    public FacturaResponse obtener(@PathVariable Long id) {
        return facturaService.obtenerPorId(id);
    }
}
