package co.edu.uniquindio.sivitrix.cliente.service;

import co.edu.uniquindio.sivitrix.cliente.dto.ClienteRequest;
import co.edu.uniquindio.sivitrix.cliente.dto.ClienteResponse;

import java.util.List;

public interface ClienteEmpresarialService {

    ClienteResponse crear(ClienteRequest request);

    List<ClienteResponse> listar(String nombre);

    ClienteResponse obtenerPorId(Long id);

    ClienteResponse actualizar(Long id, ClienteRequest request);

    void eliminar(Long id);
}
