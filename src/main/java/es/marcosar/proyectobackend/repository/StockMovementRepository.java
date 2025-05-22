package es.marcosar.proyectobackend.repository;

import es.marcosar.proyectobackend.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByProductIdOrderByMovementDateDesc(Long productId);

    List<StockMovement> findAllByOrderByMovementDateDesc();

    @Query("SELECT sm.type as type, COUNT(sm) as count FROM StockMovement sm WHERE sm.movementDate >= :startDate GROUP BY sm.type")
    List<Map<String, Object>> countMovementsByTypeSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT SUM(CASE WHEN sm.type = 'ENTRADA' OR sm.type = 'AJUSTE_INICIAL' OR (sm.type = 'CORRECCION' AND sm.quantityChanged > 0) THEN sm.quantityChanged ELSE 0 END) as totalEntradas, " +
            "SUM(CASE WHEN sm.type = 'SALIDA' OR (sm.type = 'CORRECCION' AND sm.quantityChanged < 0) THEN ABS(sm.quantityChanged) ELSE 0 END) as totalSalidas " +
            "FROM StockMovement sm WHERE sm.movementDate >= :startDate AND sm.movementDate <= :endDate")
    Map<String, Long> getMovementSummaryBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}