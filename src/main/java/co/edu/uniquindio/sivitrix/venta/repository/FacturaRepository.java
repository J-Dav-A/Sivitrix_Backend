package co.edu.uniquindio.sivitrix.venta.repository;

import co.edu.uniquindio.sivitrix.venta.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FacturaRepository extends JpaRepository<Factura, Long> {

    Optional<Factura> findByVentaId(Long ventaId);

    List<Factura> findAllByOrderByFechaEmisionDesc();
}
