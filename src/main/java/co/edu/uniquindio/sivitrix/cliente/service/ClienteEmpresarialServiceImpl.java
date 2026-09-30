package co.edu.uniquindio.sivitrix.cliente.service;

import co.edu.uniquindio.sivitrix.cliente.entity.ClienteEmpresarial;
import co.edu.uniquindio.sivitrix.cliente.mapper.ClienteMapper;
import co.edu.uniquindio.sivitrix.cliente.dto.ClienteRequest;
import co.edu.uniquindio.sivitrix.cliente.dto.ClienteResponse;
import co.edu.uniquindio.sivitrix.cliente.repository.ClienteEmpresarialRepository;
import co.edu.uniquindio.sivitrix.common.exception.DuplicateResourceException;
import co.edu.uniquindio.sivitrix.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.uniquindio.sivitrix.common.util.FuzzySearchUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteEmpresarialServiceImpl implements ClienteEmpresarialService {

    private final ClienteEmpresarialRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    public ClienteResponse crear(ClienteRequest request) {
        // SWR-01: el NIT debe ser unico en el sistema.
        if (clienteRepository.existsByNit(request.nit())) {
            throw new DuplicateResourceException(
                    "Ya existe un cliente empresarial registrado con el NIT '" + request.nit() + "'");
        }

        ClienteEmpresarial cliente = clienteMapper.toEntity(request);
        cliente.setSaldoCredito(BigDecimal.ZERO.setScale(2)); // SWR-37: inicia sin creditos activos

        ClienteEmpresarial guardado = clienteRepository.save(cliente);
        return clienteMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listar(String nombre) {
        List<ClienteEmpresarial> clientes = clienteRepository.findAll();

        if (nombre != null && !nombre.isBlank()) {
            clientes = clientes.stream()
                    .filter(c -> c.getNit().toLowerCase().contains(nombre.toLowerCase())
                            || FuzzySearchUtils.coincideAproximado(c.getNombre(), nombre))
                    .toList();
        }

        return clientes.stream().map(clienteMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse obtenerPorId(Long id) {
        return clienteMapper.toResponse(buscarOFallar(id));
    }

    @Override
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        ClienteEmpresarial cliente = buscarOFallar(id);
        // SWR-01: el NIT no se modifica en una actualizacion (ver mapper).
        clienteMapper.actualizarDesdeRequest(request, cliente);
        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    @Override
    public void eliminar(Long id) {
        ClienteEmpresarial cliente = buscarOFallar(id);
        clienteRepository.delete(cliente);
    }

    private ClienteEmpresarial buscarOFallar(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro el cliente empresarial con id " + id));
    }
}
