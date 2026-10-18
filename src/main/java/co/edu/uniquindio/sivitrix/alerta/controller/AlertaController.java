package co.edu.uniquindio.sivitrix.alerta.controller;

import co.edu.uniquindio.sivitrix.alerta.dto.AlertaResponse;
import co.edu.uniquindio.sivitrix.alerta.service.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** F-07: consulta de alertas de bajo stock. */
@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping
    public List<AlertaResponse> listar(@RequestParam(defaultValue = "true") boolean soloActivas) {
        return alertaService.listar(soloActivas);
    }
}
