package co.edu.uniquindio.sivitrix.cliente.mapper;

import co.edu.uniquindio.sivitrix.cliente.dto.ClienteRequest;
import co.edu.uniquindio.sivitrix.cliente.dto.ClienteResponse;
import co.edu.uniquindio.sivitrix.cliente.entity.ClienteEmpresarial;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "saldoCredito", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "actualizadoEn", ignore = true)
    ClienteEmpresarial toEntity(ClienteRequest request);

    @Mapping(target = "cupoDisponible",
            expression = "java(cliente.getLimiteCredito().subtract(cliente.getSaldoCredito()).max(java.math.BigDecimal.ZERO))")
    ClienteResponse toResponse(ClienteEmpresarial cliente);

    // SWR-01 (actualizar): el NIT no se toca en una actualizacion.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nit", ignore = true)
    @Mapping(target = "saldoCredito", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "actualizadoEn", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void actualizarDesdeRequest(ClienteRequest request, @MappingTarget ClienteEmpresarial cliente);
}
