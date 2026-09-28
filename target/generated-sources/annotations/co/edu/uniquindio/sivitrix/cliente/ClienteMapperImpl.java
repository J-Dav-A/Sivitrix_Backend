package co.edu.uniquindio.sivitrix.cliente;

import co.edu.uniquindio.sivitrix.cliente.dto.ClienteRequest;
import co.edu.uniquindio.sivitrix.cliente.dto.ClienteResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-27T21:48:29-0500",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class ClienteMapperImpl implements ClienteMapper {

    @Override
    public ClienteEmpresarial toEntity(ClienteRequest request) {
        if ( request == null ) {
            return null;
        }

        ClienteEmpresarial.ClienteEmpresarialBuilder clienteEmpresarial = ClienteEmpresarial.builder();

        clienteEmpresarial.nit( request.nit() );
        clienteEmpresarial.nombre( request.nombre() );
        clienteEmpresarial.telefono( request.telefono() );
        clienteEmpresarial.direccion( request.direccion() );
        clienteEmpresarial.email( request.email() );
        clienteEmpresarial.contactoNombre( request.contactoNombre() );
        clienteEmpresarial.contactoCedula( request.contactoCedula() );

        return clienteEmpresarial.build();
    }

    @Override
    public ClienteResponse toResponse(ClienteEmpresarial cliente) {
        if ( cliente == null ) {
            return null;
        }

        Long id = null;
        String nit = null;
        String nombre = null;
        String telefono = null;
        String direccion = null;
        String email = null;
        String contactoNombre = null;
        String contactoCedula = null;
        BigDecimal saldoCredito = null;
        LocalDateTime creadoEn = null;
        LocalDateTime actualizadoEn = null;

        id = cliente.getId();
        nit = cliente.getNit();
        nombre = cliente.getNombre();
        telefono = cliente.getTelefono();
        direccion = cliente.getDireccion();
        email = cliente.getEmail();
        contactoNombre = cliente.getContactoNombre();
        contactoCedula = cliente.getContactoCedula();
        saldoCredito = cliente.getSaldoCredito();
        creadoEn = cliente.getCreadoEn();
        actualizadoEn = cliente.getActualizadoEn();

        ClienteResponse clienteResponse = new ClienteResponse( id, nit, nombre, telefono, direccion, email, contactoNombre, contactoCedula, saldoCredito, creadoEn, actualizadoEn );

        return clienteResponse;
    }

    @Override
    public void actualizarDesdeRequest(ClienteRequest request, ClienteEmpresarial cliente) {
        if ( request == null ) {
            return;
        }

        cliente.setNombre( request.nombre() );
        cliente.setTelefono( request.telefono() );
        cliente.setDireccion( request.direccion() );
        cliente.setEmail( request.email() );
        cliente.setContactoNombre( request.contactoNombre() );
        cliente.setContactoCedula( request.contactoCedula() );
    }
}
