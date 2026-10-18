package co.edu.uniquindio.sivitrix.credito.repository;

import co.edu.uniquindio.sivitrix.credito.entity.Credito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditoRepository extends JpaRepository<Credito, Long> {

    List<Credito> findAllByOrderByFechaSolicitudDesc();
}
