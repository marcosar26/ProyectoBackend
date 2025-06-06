package es.marcosar.proyectobackend.dto;

import es.marcosar.proyectobackend.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserRequestDTO {
    @NotBlank
    public String username;
    @NotBlank // Para creación, opcional para actualización si no se cambia
    public String password;
    @NotNull
    public User.Role role;
    public String name;
}
