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

    // Método auxiliar para obtener el usuario autenticado actual
    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null; // O lanzar excepción si el usuario es mandatorio para el movimiento
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null); // Podrías manejar mejor el caso de no encontrarlo
    }


    // Método auxiliar para registrar un movimiento de stock
    private void recordStockMovement(Product product, StockMovement.MovementType type, int quantityChanged, int stockBefore, int stockAfter, String reason) {
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setType(type);
        movement.setQuantityChanged(quantityChanged);
        movement.setStockBefore(stockBefore);
        movement.setStockAfter(stockAfter);
        movement.setMovementDate(LocalDateTime.now());
        movement.setReason(reason);
        movement.setUser(getCurrentAuthenticatedUser()); // Asigna el usuario actual
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
        // Al crear, el stock inicial es el que se define en el producto
        int initialStock = product.getStock() != null ? product.getStock() : 0;
        product.setStock(initialStock); // Asegurar que el stock no sea null

        Product savedProduct = productRepository.save(product);

        // Registrar el movimiento de stock inicial
        if (initialStock > 0) {
            recordStockMovement(savedProduct, StockMovement.MovementType.AJUSTE_INICIAL, initialStock, 0, initialStock, "Creación de producto");
        } else {
            // Si el stock inicial es 0, podrías registrarlo o no, según prefieras.
            // recordStockMovement(savedProduct, StockMovement.MovementType.AJUSTE_INICIAL, 0, 0, 0, "Creación de producto (stock 0)");
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
                    existingProduct.setStock(newStock); // Actualizar stock
                    existingProduct.setImageUrl(productDetails.getImageUrl());

                    Product updatedProduct = productRepository.save(existingProduct);

                    // Registrar movimiento si el stock cambió
                    if (stockBefore != newStock) {
                        int quantityChanged = newStock - stockBefore;
                        StockMovement.MovementType type = quantityChanged > 0 ? StockMovement.MovementType.ENTRADA : StockMovement.MovementType.SALIDA;
                        if (quantityChanged < 0)
                            quantityChanged = Math.abs(quantityChanged); // Guardar como cantidad positiva para SALIDA

                        // Adaptar el motivo según la acción, aquí es genérico "Actualización de producto"
                        // En un sistema más complejo, el motivo vendría del contexto (venta, compra, ajuste manual).
                        String reason = "Actualización de producto";
                        if (type == StockMovement.MovementType.ENTRADA && quantityChanged == newStock - stockBefore)
                            reason = "Ajuste de stock (Entrada)";
                        if (type == StockMovement.MovementType.SALIDA && quantityChanged == stockBefore - newStock)
                            reason = "Ajuste de stock (Salida)";

                        // Para la tabla de movimientos, quantityChanged se refiere a la magnitud del cambio.
                        // Si es una salida, el stock disminuye.
                        // Reajustamos quantityChanged para que sea positivo si es salida, y el tipo indica la dirección.
                        int actualQuantityForRecord = newStock - stockBefore; // Positivo para entrada, negativo para salida

                        recordStockMovement(updatedProduct, type, actualQuantityForRecord, stockBefore, newStock, reason);
                    }
                    return updatedProduct;
                });
    }

    @Transactional
    public boolean deleteProduct(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            // Opcional: Registrar un movimiento de "SALIDA_TOTAL" o "ELIMINACION" antes de borrar,
            // aunque esto puede ser problemático si luego quieres consultar el producto.
            // Otra opción es no borrar físicamente, sino marcar como "desactivado".
            // Por ahora, solo borramos.
            // Si se borra, los movimientos de stock con este product_id podrían quedar huérfanos
            // o podrías configurar ON DELETE CASCADE (con cuidado).
            // Alternativa: No borrar movimientos, o marcarlos.

            // Antes de borrar el producto, podrías registrar una salida final de su stock si es > 0
            // if (product.getStock() > 0) {
            //     recordStockMovement(product, StockMovement.MovementType.SALIDA, product.getStock() * -1, product.getStock(), 0, "Eliminación de producto");
            // }
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