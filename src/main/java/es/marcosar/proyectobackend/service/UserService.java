package es.marcosar.proyectobackend.service;

import es.marcosar.proyectobackend.entity.User;
import es.marcosar.proyectobackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // @Autowired
    // private PasswordEncoder passwordEncoder; // Descomentar si usas Spring Security

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario '" + user.getUsername() + "' ya está en uso.");
        }
        // En un sistema real, hashear la contraseña ANTES de guardarla
        // user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public Optional<User> updateUser(Long id, User userDetails) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    if (userDetails.getUsername() != null && !userDetails.getUsername().equals(existingUser.getUsername())) {
                        if (userRepository.existsByUsername(userDetails.getUsername())) {
                            throw new IllegalArgumentException("El nuevo nombre de usuario '" + userDetails.getUsername() + "' ya está en uso.");
                        }
                        existingUser.setUsername(userDetails.getUsername());
                    }
                    if (userDetails.getName() != null) {
                        existingUser.setName(userDetails.getName());
                    }
                    if (userDetails.getRole() != null) {
                        existingUser.setRole(userDetails.getRole());
                    }
                    // Actualización de contraseña (si se provee y es diferente)
                    // Considerar lógica para no actualizar si la contraseña está vacía en la petición
                    if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                        // En un sistema real, hashear la nueva contraseña
                        // existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                        existingUser.setPassword(userDetails.getPassword()); // Temporalmente sin hashear
                    }
                    return userRepository.save(existingUser);
                });
    }

    @Transactional
    public boolean deleteUser(Long id) {
        // Podrías añadir lógica para no permitir eliminar al último admin, o al usuario logueado.
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}