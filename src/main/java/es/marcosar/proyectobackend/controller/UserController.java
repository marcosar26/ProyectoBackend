package es.marcosar.proyectobackend.controller;

import es.marcosar.proyectobackend.dto.UserRequestDTO;
import es.marcosar.proyectobackend.dto.UserResponseDTO;
import es.marcosar.proyectobackend.entity.User;
import es.marcosar.proyectobackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
// @CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

    // Convertir User a UserResponseDTO
    private UserResponseDTO convertToDTO(User user) {
        return new UserResponseDTO(user);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> userDTOs = userService.getAllUsers().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(convertToDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Nota: Este endpoint crea un usuario con contraseña.
    // En un sistema real, el login sería un endpoint separado (/api/auth/login)
    // que verifica credenciales y devuelve un token (JWT).
    // El CRUD de usuarios sería para administradores.
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        try {
            User user = new User();
            user.setUsername(userRequestDTO.username);
            user.setPassword(userRequestDTO.password); // Sin hashear por ahora
            user.setRole(userRequestDTO.role);
            user.setName(userRequestDTO.name);
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(createdUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        // Para la actualización, la contraseña en UserRequestDTO podría ser opcional
        // si el DTO se ajusta o se maneja la lógica en el servicio.
        try {
            User userDetails = new User();
            userDetails.setUsername(userRequestDTO.username);
            // Solo actualizar contraseña si se proporciona en el DTO
            if (userRequestDTO.password != null && !userRequestDTO.password.isEmpty()) {
                userDetails.setPassword(userRequestDTO.password); // Sin hashear por ahora
            }
            userDetails.setRole(userRequestDTO.role);
            userDetails.setName(userRequestDTO.name);

            return userService.updateUser(id, userDetails)
                    .map(updatedUser -> ResponseEntity.ok(convertToDTO(updatedUser)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}