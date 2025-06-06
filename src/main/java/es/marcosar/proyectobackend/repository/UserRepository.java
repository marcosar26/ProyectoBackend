package es.marcosar.proyectobackend.repository;

import es.marcosar.proyectobackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // Para buscar por nombre de usuario (login, validación)

    boolean existsByUsername(String username);
}