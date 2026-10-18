package co.edu.uniquindio.sivitrix.alerta.repository;

import co.edu.uniquindio.sivitrix.alerta.entity.AlertaStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlertaStockRepository extends JpaRepository<AlertaStock, Long> {

    Optional<AlertaStock> findFirstByProductoIdAndActivaTrue(Long productoId);

    List<AlertaStock> findByActivaOrderByFechaGeneracionDesc(Boolean activa);

    List<AlertaStock> findAllByOrderByFechaGeneracionDesc();
}

