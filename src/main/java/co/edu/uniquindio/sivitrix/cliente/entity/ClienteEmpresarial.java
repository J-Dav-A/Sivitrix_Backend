package co.edu.uniquindio.sivitrix.cliente.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SWR-01, SWR-35: cliente empresarial (taller, negocio o empresa), NIT unico.
 * SWR-36: datos de la persona de contacto autorizada.
 * SWR-37: saldoCredito se recalculara a partir de los creditos activos del
 * cliente cuando se implemente la funcionalidad F-05 (Autorizar venta a
 * credito); por ahora se persiste con 0 y se actualizara desde ese modulo.
 */
@Entity
@Table(name = "cliente_empresarial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteEmpresarial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String nit;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(name = "contacto_nombre", nullable = false, length = 150)
    private String contactoNombre;

    @Column(name = "contacto_cedula", nullable = false, length = 20)
    private String contactoCedula;

    @Column(name = "saldo_credito", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoCredito;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.creadoEn = now;
        this.actualizadoEn = now;
        if (this.saldoCredito == null) {
            this.saldoCredito = BigDecimal.ZERO.setScale(2);
        }
    }

    @PreUpdate
    void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}
