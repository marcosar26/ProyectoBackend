package es.marcosar.proyectobackend.config;

import es.marcosar.proyectobackend.entity.Product;
import es.marcosar.proyectobackend.entity.User;
import es.marcosar.proyectobackend.repository.ProductRepository;
import es.marcosar.proyectobackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Crear Productos si no existen
        if (productRepository.count() == 0) {
            Product p1 = new Product(null, "Laptop Pro X desde Backend", "Potente laptop para profesionales", 1250.00, 12, "https://via.placeholder.com/150/0000FF/808080?Text=LaptopBE");
            Product p2 = new Product(null, "Smartphone Z Backend", "Última generación de smartphone", 850.00, 22, "https://via.placeholder.com/150/FF0000/FFFFFF?Text=SmartphoneBE");
            Product p3 = new Product(null, "Auriculares BT Backend", "Sonido inmersivo y sin cables", 155.00, 45, "https://via.placeholder.com/150/008000/FFFFFF?Text=AuricularesBE");
            productRepository.saveAll(Arrays.asList(p1, p2, p3));
            System.out.println(">>> Productos de prueba creados.");
        }

        // Crear Usuarios si no existen
        if (userRepository.count() == 0) {
            User admin = new User(null, "admin", passwordEncoder.encode("admin"), User.Role.ADMIN, "Admin Supremo");
            User manager = new User(null, "manager", passwordEncoder.encode("manager"), User.Role.MANAGER, "Jefe de Tienda");
            User user = new User(null, "user", passwordEncoder.encode("user"), User.Role.USER, "Cliente Fiel");
            userRepository.saveAll(Arrays.asList(admin, manager, user));
            System.out.println(">>> Usuarios de prueba (con contraseñas hasheadas) creados.");
        }
    }
}