package co.edu.uniquindio.sivitrix.cliente;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteEmpresarialRepository extends JpaRepository<ClienteEmpresarial, Long> {

    boolean existsByNit(String nit);

    Optional<ClienteEmpresarial> findByNit(String nit);

    List<ClienteEmpresarial> findByNombreContainingIgnoreCase(String nombre);
}
