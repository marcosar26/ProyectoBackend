package es.marcosar.proyectobackend.controller;

import es.marcosar.proyectobackend.dto.StockMovementResponseDTO;
import es.marcosar.proyectobackend.entity.StockMovement;
import es.marcosar.proyectobackend.service.StockMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stock-movements")
public class StockMovementController {

    @Autowired
    private StockMovementService stockMovementService;

    // Método para convertir StockMovement a DTO
    private StockMovementResponseDTO convertToDTO(StockMovement movement) {
        return new StockMovementResponseDTO(movement);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')") // Solo Admin y Manager pueden ver todos los movimientos
    public ResponseEntity<List<StockMovementResponseDTO>> getAllStockMovements() {
        List<StockMovementResponseDTO> movementDTOs = stockMovementService.getAllMovements().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(movementDTOs);
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    // O también 'USER' si los usuarios pueden ver el historial de un producto
    public ResponseEntity<List<StockMovementResponseDTO>> getStockMovementsByProductId(@PathVariable Long productId) {
        List<StockMovementResponseDTO> movementDTOs = stockMovementService.getMovementsByProductId(productId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(movementDTOs);
    }

    @GetMapping("/stats/movements-by-type")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<List<Map<String, Object>>> getMovementsByTypeLast30Days() {
        // Necesitarías un método en StockMovementService que agrupe por tipo y cuente
        // Esto puede ser una consulta JPQL o Criteria API más compleja.
        // Ejemplo de lo que devolvería el servicio: List<Map<String, Object>> donde cada mapa es {"type": "ENTRADA", "count": 25}
        List<Map<String, Object>> stats = stockMovementService.countMovementsByTypeLastDays(30);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/summary-last-week")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, Long>> getStockMovementSummaryLastWeek() {
        Map<String, Long> summary = stockMovementService.getMovementSummaryForPeriod(
                LocalDate.now().minusWeeks(1),
                LocalDate.now()
        );
        // El summary podría ser: {"totalEntradas": 50, "totalSalidas": 30}
        return ResponseEntity.ok(summary);
    }
}