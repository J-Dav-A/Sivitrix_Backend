package co.edu.uniquindio.sivitrix.seguridad;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ContextoSeguridad contexto;

    @GetMapping("/me")
    public Map<String, String> yo(Authentication auth) {
        String usuario = auth != null ? auth.getName() : "anonimo";
        return Map.of("usuario", usuario, "rol", contexto.esAdministrador() ? "ADMINISTRADOR" : "VENDEDOR");
    }
}