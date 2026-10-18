package co.edu.uniquindio.sivitrix.venta.repository;

import co.edu.uniquindio.sivitrix.venta.entity.EstadoVenta;
import co.edu.uniquindio.sivitrix.venta.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findAllByOrderByFechaDesc();

    List<Venta> findByEstadoAndFechaBetween(EstadoVenta estado, LocalDateTime desde, LocalDateTime hasta);

    // Numeracion consecutiva VIT-AAAA-##### (secuencia de PostgreSQL, ver V6).
    @Query(value = "select nextval('venta_numero_seq')", nativeQuery = true)
    Long siguienteNumero();
}
