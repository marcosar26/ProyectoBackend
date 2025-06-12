package es.marcosar.proyectobackend.repository;

import es.marcosar.proyectobackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    long count();

    long countByStockLessThan(Integer stockThreshold);
}