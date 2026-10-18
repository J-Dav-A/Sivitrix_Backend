package co.edu.uniquindio.sivitrix.credito.service;

import co.edu.uniquindio.sivitrix.credito.dto.CreditoResponse;
import co.edu.uniquindio.sivitrix.credito.entity.EstadoCredito;

import java.util.List;

public interface CreditoService {

    List<CreditoResponse> listar(EstadoCredito estado, Long clienteId);

    CreditoResponse obtenerPorId(Long id);

    CreditoResponse autorizar(Long id);

    CreditoResponse rechazar(Long id, String motivo);

    CreditoResponse pagarCuota(Long cuotaId);
}