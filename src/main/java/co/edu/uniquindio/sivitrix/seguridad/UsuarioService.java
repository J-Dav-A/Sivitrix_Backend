package co.edu.uniquindio.sivitrix.seguridad;

import co.edu.uniquindio.sivitrix.common.exception.BusinessRuleException;
import co.edu.uniquindio.sivitrix.common.exception.DuplicateResourceException;
import co.edu.uniquindio.sivitrix.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return repository.findAllByOrderByIdAsc().stream().map(this::toResponse).toList();
    }

    public UsuarioResponse crear(UsuarioRequest request) {
        String username = request.username().trim().toLowerCase();
        if (repository.existsByUsername(username)) {
            throw new DuplicateResourceException("Ya existe un usuario con el nombre '" + username + "'");
        }
        return toResponse(guardar(username, request.password(), request.rol(), false));
    }

    public void eliminar(Long id, String usuarioActual) {
        Usuario u = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el usuario con id " + id));
        if (u.isPrincipal()) {
            throw new BusinessRuleException("El administrador principal no se puede eliminar");
        }
        if (u.getUsername().equalsIgnoreCase(usuarioActual)) {
            throw new BusinessRuleException("No puedes eliminar el usuario con el que iniciaste sesion");
        }
        repository.delete(u);
    }

    /** Usado al arrancar para crear los usuarios iniciales. */
    public Usuario guardar(String username, String clave, RolUsuario rol, boolean principal) {
        Usuario u = new Usuario();
        u.setUsername(username.trim().toLowerCase());
        u.setPassword(passwordEncoder.encode(clave));
        u.setRol(rol);
        u.setPrincipal(principal);
        u.setCreadoEn(LocalDateTime.now());
        return repository.save(u);
    }

    private UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getUsername(), u.getRol(), u.isPrincipal(), u.getCreadoEn());
    }
}