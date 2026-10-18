package co.edu.uniquindio.sivitrix.reporte.controller;

import co.edu.uniquindio.sivitrix.reporte.dto.*;
import co.edu.uniquindio.sivitrix.reporte.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/** F-08: reportes gerenciales. Fechas en formato AAAA-MM-DD. Por defecto, los ultimos 30 dias. */
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/ventas")
    public ReporteVentasResponse ventas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.ventas(desdeOpt(desde), hastaOpt(hasta));
    }

    @GetMapping("/productos-mas-vendidos")
    public List<ProductoVendidoResponse> productos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "10") int limite) {
        return reporteService.productosMasVendidos(desdeOpt(desde), hastaOpt(hasta), limite);
    }

    @GetMapping("/mejores-clientes")
    public List<ClienteCompraResponse> clientes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "10") int limite) {
        return reporteService.mejoresClientes(desdeOpt(desde), hastaOpt(hasta), limite);
    }

    @GetMapping("/inventario")
    public ReporteInventarioResponse inventario() {
        return reporteService.inventario();
    }

    private LocalDate desdeOpt(LocalDate desde) {
        return desde != null ? desde : LocalDate.now().minusDays(30);
    }

    private LocalDate hastaOpt(LocalDate hasta) {
        return hasta != null ? hasta : LocalDate.now();
    }
}
