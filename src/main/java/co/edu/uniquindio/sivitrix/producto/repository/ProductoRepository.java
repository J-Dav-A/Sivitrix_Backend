package co.edu.uniquindio.sivitrix.producto.repository;

import co.edu.uniquindio.sivitrix.producto.entity.CategoriaProducto;
import co.edu.uniquindio.sivitrix.producto.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByCodigo(String codigo);

    Optional<Producto> findByCodigo(String codigo);

    java.util.List<Producto> findByCategoria(CategoriaProducto categoria);
}
