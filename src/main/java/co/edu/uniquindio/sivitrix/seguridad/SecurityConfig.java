package co.edu.uniquindio.sivitrix.seguridad;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SeguridadProperties.class)
public class SecurityConfig {

    static final String ADMIN = "ADMINISTRADOR";
    static final String VENDEDOR = "VENDEDOR";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, SeguridadProperties props) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (!props.habilitada()) {
            http.authorizeHttpRequests(a -> a.anyRequest().permitAll());
            return http.build();
        }

        http.authorizeHttpRequests(a -> a
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/creditos/**", "/api/reportes/**",
                                "/api/proveedores/**", "/api/pedidos/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/ventas/*/cancelar").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/inventario/movimientos").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/productos").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole(ADMIN)
                        .anyRequest().authenticated())
                .httpBasic(basic -> basic.authenticationEntryPoint((req, res, ex) -> {
                    res.setStatus(401);
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write("{\"status\":401,\"message\":\"Usuario o clave incorrectos\"}");
                }))
                .exceptionHandling(e -> e.accessDeniedHandler((req, res, ex) -> {
                    res.setStatus(403);
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write("{\"status\":403,\"message\":\"No tienes permiso para realizar esta accion\"}");
                }));
        return http.build();
    }

    @Bean
    public UserDetailsService usuarios(SeguridadProperties props) {
        return new InMemoryUserDetailsManager(
                User.withUsername(props.adminUsuario()).password("{noop}" + props.adminClave())
                        .roles(ADMIN).build(),
                User.withUsername(props.vendedorUsuario()).password("{noop}" + props.vendedorClave())
                        .roles(VENDEDOR).build());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:4200"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", c);
        return source;
    }
}
