package co.edu.uniquindio.sivitrix.seguridad;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Usuario del sistema. La clave se guarda cifrada con BCrypt. */
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolUsuario rol;

    // El administrador principal no se puede eliminar.
    @Column(nullable = false)
    private boolean principal;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;
}
