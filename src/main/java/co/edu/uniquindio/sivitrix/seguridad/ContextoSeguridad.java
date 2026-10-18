package co.edu.uniquindio.sivitrix.seguridad;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContextoSeguridad {

    private final SeguridadProperties props;

    public boolean esAdministrador() {
        if (!props.habilitada()) {
            return true;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + SecurityConfig.ADMIN));
    }
}
