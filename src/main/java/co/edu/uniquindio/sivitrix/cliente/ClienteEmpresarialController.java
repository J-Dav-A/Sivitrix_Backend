package co.edu.uniquindio.sivitrix.cliente;

import co.edu.uniquindio.sivitrix.cliente.dto.ClienteRequest;
import co.edu.uniquindio.sivitrix.cliente.dto.ClienteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * SWR-01, SWR-35, SWR-36, SWR-37: registro y consulta de clientes
 * empresariales.
 */
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteEmpresarialController {

    private final ClienteEmpresarialService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse creado = clienteService.crear(request);
        return ResponseEntity.created(URI.create("/api/clientes/" + creado.id())).body(creado);
    }

    @GetMapping
    public List<ClienteResponse> listar(@RequestParam(required = false) String nombre) {
        return clienteService.listar(nombre);
    }

    @GetMapping("/{id}")
    public ClienteResponse obtener(@PathVariable Long id) {
        return clienteService.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public ClienteResponse actualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        return clienteService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
    }
}
