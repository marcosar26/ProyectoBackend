package es.marcosar.proyectobackend.dto;

import es.marcosar.proyectobackend.entity.StockMovement;

import java.time.LocalDateTime;

public class StockMovementResponseDTO {
    public Long id;
    public Long productId;
    public String productName;
    public StockMovement.MovementType type;
    public Integer quantityChanged;
    public Integer stockBefore;
    public Integer stockAfter;
    public LocalDateTime movementDate;
    public String reason;
    public String username;

    public StockMovementResponseDTO(StockMovement movement) {
        this.id = movement.getId();
        if (movement.getProduct() != null) {
            this.productId = movement.getProduct().getId();
            this.productName = movement.getProduct().getName();
        }
        this.type = movement.getType();
        this.quantityChanged = movement.getQuantityChanged();
        this.stockBefore = movement.getStockBefore();
        this.stockAfter = movement.getStockAfter();
        this.movementDate = movement.getMovementDate();
        this.reason = movement.getReason();
        if (movement.getUser() != null) {
            this.username = movement.getUser().getUsername();
        } else {
            this.username = "Sistema";
        }
    }
}