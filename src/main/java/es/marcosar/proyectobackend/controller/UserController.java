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
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

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

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        try {
            User user = new User();
            user.setUsername(userRequestDTO.username);
            user.setPassword(userRequestDTO.password);
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
        try {
            User userDetails = new User();
            userDetails.setUsername(userRequestDTO.username);
            if (userRequestDTO.password != null && !userRequestDTO.password.isEmpty()) {
                userDetails.setPassword(userRequestDTO.password);
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