package co.edu.uniquindio.sivitrix.pedido.repository;

import co.edu.uniquindio.sivitrix.pedido.entity.EstadoPedido;
import co.edu.uniquindio.sivitrix.pedido.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findAllByOrderByFechaPedidoDesc();

    List<Pedido> findByProveedorIdOrderByFechaPedidoDesc(Long proveedorId);

    // SWR-14: un proveedor con pedidos activos no se puede eliminar.
    boolean existsByProveedorIdAndEstadoIn(Long proveedorId, Collection<EstadoPedido> estados);
}
