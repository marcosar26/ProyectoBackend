package es.marcosar.proyectobackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username") // Username debe ser único
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 2, message = "La contraseña debe tener al menos 2 caracteres")
    // En un sistema real, se almacenaría hasheada
    @Column(nullable = false)
    private String password; // ¡¡¡IMPORTANTE: Esto debería estar hasheado en un sistema real!!!

    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING) // Almacena el enum como String ('ADMIN', 'MANAGER', 'USER')
    @Column(nullable = false, length = 20)
    private Role role;

    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    @Column(length = 100)
    private String name;

    // Enum para los roles
    public enum Role {
        ADMIN, MANAGER, USER
    }
}