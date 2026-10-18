package co.edu.uniquindio.sivitrix.seguridad;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sivitrix.seguridad")
public record SeguridadProperties(
        boolean habilitada,
        String adminUsuario,
        String adminClave,
        String vendedorUsuario,
        String vendedorClave
) {
}