package es.marcosar.proyectobackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Muchos movimientos pueden estar asociados a un producto
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    private Product product;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MovementType type; // ENTRADA, SALIDA, AJUSTE_INICIAL, CORRECCION

    @NotNull(message = "La cantidad es obligatoria")
    @Column(nullable = false)
    private Integer quantityChanged; // Cantidad que se sumó o restó. Positivo para entrada, negativo para salida.

    @Column(nullable = false)
    private Integer stockBefore; // Stock antes del movimiento

    @Column(nullable = false)
    private Integer stockAfter; // Stock después del movimiento

    @NotNull(message = "La fecha del movimiento es obligatoria")
    @Column(nullable = false)
    private LocalDateTime movementDate;

    @Column(length = 255)
    private String reason; // Motivo del movimiento (ej. "Venta ID:123", "Compra a proveedor X", "Ajuste de inventario")

    @ManyToOne(fetch = FetchType.LAZY) // Opcional: qué usuario realizó el movimiento
    @JoinColumn(name = "user_id")
    private User user; // El usuario que registró el movimiento

    public enum MovementType {
        ENTRADA,      // Ingreso de stock (compra, devolución de cliente)
        SALIDA,       // Salida de stock (venta, merma)
        AJUSTE_INICIAL, // Stock inicial al crear el producto
        CORRECCION    // Ajuste manual de inventario
    }

    @PrePersist
    protected void onCreate() {
        if (this.movementDate == null) {
            this.movementDate = LocalDateTime.now();
        }
    }
}