package co.edu.uniquindio.sivitrix.alerta.service;

import co.edu.uniquindio.sivitrix.alerta.dto.AlertaResponse;
import co.edu.uniquindio.sivitrix.producto.entity.Producto;

import java.util.List;

public interface AlertaService {

    /**
     * Revisa el stock del producto y crea, actualiza o cierra su alerta.
     * Se invoca cada vez que cambia el stock o el stock minimo.
     */
    void evaluar(Producto producto);

    List<AlertaResponse> listar(boolean soloActivas);
}
