package co.edu.uniquindio.sivitrix.credito.controller;

import co.edu.uniquindio.sivitrix.credito.dto.CreditoResponse;
import co.edu.uniquindio.sivitrix.credito.dto.RechazoRequest;
import co.edu.uniquindio.sivitrix.credito.entity.EstadoCredito;
import co.edu.uniquindio.sivitrix.credito.service.CreditoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * F-05: autorizacion de ventas a credito, plan de cuotas y pagos.
 * NOTA (SWR-26): cuando se active Spring Security, autorizar/rechazar
 * debe quedar restringido al rol Administrador.
 */
@RestController
@RequestMapping("/api/creditos")
@RequiredArgsConstructor
public class CreditoController {

    private final CreditoService creditoService;

    @GetMapping
    public List<CreditoResponse> listar(@RequestParam(required = false) EstadoCredito estado,
                                        @RequestParam(required = false) Long clienteId) {
        return creditoService.listar(estado, clienteId);
    }

    @GetMapping("/{id}")
    public CreditoResponse obtener(@PathVariable Long id) {
        return creditoService.obtenerPorId(id);
    }

    @PostMapping("/{id}/autorizar")
    public CreditoResponse autorizar(@PathVariable Long id) {
        return creditoService.autorizar(id);
    }

    @PostMapping("/{id}/rechazar")
    public CreditoResponse rechazar(@PathVariable Long id, @Valid @RequestBody(required = false) RechazoRequest request) {
        return creditoService.rechazar(id, request != null ? request.motivo() : null);
    }

    @PostMapping("/cuotas/{cuotaId}/pagos")
    public CreditoResponse pagarCuota(@PathVariable Long cuotaId) {
        return creditoService.pagarCuota(cuotaId);
    }
}
