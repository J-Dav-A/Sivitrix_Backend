package co.edu.uniquindio.sivitrix.alerta.entity;

import co.edu.uniquindio.sivitrix.producto.entity.Producto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * F-07: alerta generada cuando el stock de un producto llega o cae por
 * debajo de su stock minimo. Queda activa hasta que el stock se recupera.
 */
@Entity
@Table(name = "alerta_stock")
@Getter
@Setter
@NoArgsConstructor
public class AlertaStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NivelAlerta nivel;

    @Column(name = "stock_al_generar", nullable = false)
    private Integer stockAlGenerar;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;

    @Column(nullable = false)
    private Boolean activa;
}