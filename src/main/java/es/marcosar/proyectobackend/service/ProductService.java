package es.marcosar.proyectobackend.service;

import es.marcosar.proyectobackend.entity.Product;
import es.marcosar.proyectobackend.entity.StockMovement;
import es.marcosar.proyectobackend.entity.User;
import es.marcosar.proyectobackend.repository.ProductRepository;
import es.marcosar.proyectobackend.repository.StockMovementRepository;
import es.marcosar.proyectobackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }

    private void recordStockMovement(Product product, StockMovement.MovementType type, int quantityChanged, int stockBefore, int stockAfter, String reason) {
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setType(type);
        movement.setQuantityChanged(quantityChanged);
        movement.setStockBefore(stockBefore);
        movement.setStockAfter(stockAfter);
        movement.setMovementDate(LocalDateTime.now());
        movement.setReason(reason);
        movement.setUser(getCurrentAuthenticatedUser());
        stockMovementRepository.save(movement);
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product createProduct(Product product) {
        int initialStock = product.getStock() != null ? product.getStock() : 0;
        product.setStock(initialStock);

        Product savedProduct = productRepository.save(product);

        if (initialStock > 0) {
            recordStockMovement(savedProduct, StockMovement.MovementType.AJUSTE_INICIAL, initialStock, 0, initialStock, "Creación de producto");
        }

        return savedProduct;
    }

    @Transactional
    public Optional<Product> updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    int stockBefore = existingProduct.getStock();
                    int newStock = productDetails.getStock() != null ? productDetails.getStock() : stockBefore;

                    existingProduct.setName(productDetails.getName());
                    existingProduct.setDescription(productDetails.getDescription());
                    existingProduct.setPrice(productDetails.getPrice());
                    existingProduct.setStock(newStock);
                    existingProduct.setImageUrl(productDetails.getImageUrl());

                    Product updatedProduct = productRepository.save(existingProduct);

                    if (stockBefore != newStock) {
                        int quantityChanged = newStock - stockBefore;
                        StockMovement.MovementType type = quantityChanged > 0 ? StockMovement.MovementType.ENTRADA : StockMovement.MovementType.SALIDA;
                        if (quantityChanged < 0)
                            quantityChanged = Math.abs(quantityChanged);

                        String reason = "Actualización de producto";
                        if (type == StockMovement.MovementType.ENTRADA && quantityChanged == newStock - stockBefore)
                            reason = "Ajuste de stock (Entrada)";
                        if (type == StockMovement.MovementType.SALIDA && quantityChanged == stockBefore - newStock)
                            reason = "Ajuste de stock (Salida)";

                        int actualQuantityForRecord = newStock - stockBefore;

                        recordStockMovement(updatedProduct, type, actualQuantityForRecord, stockBefore, newStock, reason);
                    }
                    return updatedProduct;
                });
    }

    @Transactional
    public boolean deleteProduct(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public long countTotalProducts() {
        return productRepository.count();
    }

    @Transactional(readOnly = true)
    public long countLowStockProducts(int threshold) {
        return productRepository.countByStockLessThan(threshold);
    }
}