package co.edu.uniquindio.sivitrix.seguridad;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/** Gestion de usuarios: solo el administrador (ver SecurityConfig). */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    public List<UsuarioResponse> listar() {
        return service.listar();
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse creado = service.crear(request);
        return ResponseEntity.created(URI.create("/api/usuarios/" + creado.id())).body(creado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id, Authentication auth) {
        service.eliminar(id, auth != null ? auth.getName() : "");
    }
}