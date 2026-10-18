package co.edu.uniquindio.sivitrix.alerta.service;

import co.edu.uniquindio.sivitrix.alerta.dto.AlertaResponse;
import co.edu.uniquindio.sivitrix.alerta.entity.AlertaStock;
import co.edu.uniquindio.sivitrix.alerta.entity.NivelAlerta;
import co.edu.uniquindio.sivitrix.alerta.repository.AlertaStockRepository;
import co.edu.uniquindio.sivitrix.producto.entity.Producto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertaServiceImpl implements AlertaService {

    private final AlertaStockRepository alertaRepository;

    @Override
    public void evaluar(Producto producto) {
        Optional<AlertaStock> activa = alertaRepository.findFirstByProductoIdAndActivaTrue(producto.getId());
        boolean bajoMinimo = producto.getStockActual() <= producto.getStockMinimo();

        if (!bajoMinimo) {
            // El stock se recupero: se cierra la alerta abierta.
            activa.ifPresent(a -> {
                a.setActiva(false);
                alertaRepository.save(a);
            });
            return;
        }

        NivelAlerta nivel = producto.getStockActual() == 0 ? NivelAlerta.CRITICO : NivelAlerta.BAJO_STOCK;
        AlertaStock alerta = activa.orElseGet(AlertaStock::new);
        alerta.setProducto(producto);
        alerta.setNivel(nivel);
        alerta.setStockAlGenerar(producto.getStockActual());
        alerta.setStockMinimo(producto.getStockMinimo());
        alerta.setActiva(true);
        if (alerta.getFechaGeneracion() == null) {
            alerta.setFechaGeneracion(LocalDateTime.now());
        }
        alertaRepository.save(alerta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaResponse> listar(boolean soloActivas) {
        List<AlertaStock> alertas = soloActivas
                ? alertaRepository.findByActivaOrderByFechaGeneracionDesc(true)
                : alertaRepository.findAllByOrderByFechaGeneracionDesc();

        // Las criticas (sin existencias) primero.
        return alertas.stream()
                .map(this::toResponse)
                .sorted(Comparator.comparing((AlertaResponse a) -> a.nivel() == NivelAlerta.CRITICO ? 0 : 1))
                .toList();
    }

    private AlertaResponse toResponse(AlertaStock a) {
        Producto p = a.getProducto();
        return new AlertaResponse(
                a.getId(),
                p.getId(),
                p.getCodigo(),
                p.getNombre(),
                a.getNivel(),
                p.getStockActual(),
                a.getStockAlGenerar(),
                a.getStockMinimo(),
                Math.max(0, p.getStockMinimo() - p.getStockActual()),
                a.getFechaGeneracion(),
                a.getActiva()
        );
    }
}
