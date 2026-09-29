package co.edu.uniquindio.sivitrix.proveedor.service;

import co.edu.uniquindio.sivitrix.proveedor.dto.ProveedorRequest;
import co.edu.uniquindio.sivitrix.proveedor.dto.ProveedorResponse;

import java.util.List;

public interface ProveedorService {

    ProveedorResponse crear(ProveedorRequest request);

    List<ProveedorResponse> listar();

    ProveedorResponse obtenerPorId(Long id);

    ProveedorResponse actualizar(Long id, ProveedorRequest request);

    void eliminar(Long id);
}