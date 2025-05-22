package es.marcosar.proyectobackend.service;

import es.marcosar.proyectobackend.entity.StockMovement;
import es.marcosar.proyectobackend.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}