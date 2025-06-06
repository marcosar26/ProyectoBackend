package es.marcosar.proyectobackend.service;

import es.marcosar.proyectobackend.entity.StockMovement;
import es.marcosar.proyectobackend.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class StockMovementService {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Transactional(readOnly = true)
    public List<StockMovement> getAllMovements() {
        return stockMovementRepository.findAllByOrderByMovementDateDesc();
    }

    @Transactional(readOnly = true)
    public List<StockMovement> getMovementsByProductId(Long productId) {
        return stockMovementRepository.findByProductIdOrderByMovementDateDesc(productId);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> countMovementsByTypeLastDays(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return stockMovementRepository.countMovementsByTypeSince(startDate);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getMovementSummaryForPeriod(LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);
        return stockMovementRepository.getMovementSummaryBetweenDates(startDateTime, endDateTime);
    }
}