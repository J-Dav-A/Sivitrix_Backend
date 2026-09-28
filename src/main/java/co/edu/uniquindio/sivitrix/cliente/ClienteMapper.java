package co.edu.uniquindio.sivitrix.cliente;

import co.edu.uniquindio.sivitrix.cliente.dto.ClienteRequest;
import co.edu.uniquindio.sivitrix.cliente.dto.ClienteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "saldoCredito", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "actualizadoEn", ignore = true)
    ClienteEmpresarial toEntity(ClienteRequest request);

    ClienteResponse toResponse(ClienteEmpresarial cliente);

    // SWR-01 (actualizar): el NIT no se toca en una actualizacion.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nit", ignore = true)
    @Mapping(target = "saldoCredito", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "actualizadoEn", ignore = true)
    void actualizarDesdeRequest(ClienteRequest request, @MappingTarget ClienteEmpresarial cliente);
}
