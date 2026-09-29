package co.edu.uniquindio.sivitrix.proveedor.repository;

import co.edu.uniquindio.sivitrix.proveedor.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    boolean existsByNit(String nit);
}