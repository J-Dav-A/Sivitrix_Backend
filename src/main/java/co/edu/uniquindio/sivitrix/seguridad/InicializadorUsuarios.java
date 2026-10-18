package co.edu.uniquindio.sivitrix.seguridad;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Al arrancar: garantiza que exista el administrador principal (no eliminable) y,
 * si la tabla estaba vacia, un vendedor de demostracion.
 */
@Component
@RequiredArgsConstructor
public class InicializadorUsuarios implements ApplicationRunner {

    private final UsuarioRepository repository;
    private final UsuarioService service;
    private final SeguridadProperties props;

    @Override
    public void run(ApplicationArguments args) {
        boolean tablaVacia = repository.count() == 0;

        if (!repository.existsByPrincipalTrue()) {
            String admin = props.adminUsuario().trim().toLowerCase();
            if (repository.existsByUsername(admin)) {
                repository.findByUsername(admin).ifPresent(u -> {
                    u.setPrincipal(true);
                    u.setRol(RolUsuario.ADMINISTRADOR);
                    repository.save(u);
                });
            } else {
                service.guardar(admin, props.adminClave(), RolUsuario.ADMINISTRADOR, true);
            }
        }

        if (tablaVacia && !repository.existsByUsername(props.vendedorUsuario().trim().toLowerCase())) {
            service.guardar(props.vendedorUsuario(), props.vendedorClave(), RolUsuario.VENDEDOR, false);
        }
    }
}