package co.edu.uniquindio.sivitrix.producto.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SWR-08, SWR-27: producto del inventario con codigo unico no editable.
 * SWR-25/SWR-26: precioVenta y costoAdquisicion se almacenan por separado;
 * el margen se calcula en el mapper (ver ProductoMapper) y su visibilidad
 * se restringe por rol a nivel de controlador/servicio (pendiente de
 * activar Spring Security).
 */
@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoriaProducto categoria;

    @Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "costo_adquisicion", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoAdquisicion;

    // SWR-30: nunca puede quedar negativo (se valida en el service).
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.creadoEn = now;
        this.actualizadoEn = now;
        if (this.stockActual == null) {
            this.stockActual = 0;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}
