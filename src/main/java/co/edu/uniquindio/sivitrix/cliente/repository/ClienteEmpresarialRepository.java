package co.edu.uniquindio.sivitrix.cliente.repository;

import co.edu.uniquindio.sivitrix.cliente.entity.ClienteEmpresarial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteEmpresarialRepository extends JpaRepository<ClienteEmpresarial, Long> {

    boolean existsByNit(String nit);

    Optional<ClienteEmpresarial> findByNit(String nit);
}
