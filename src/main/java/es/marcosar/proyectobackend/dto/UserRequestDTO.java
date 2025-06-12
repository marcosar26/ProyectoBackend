package es.marcosar.proyectobackend.dto;

import es.marcosar.proyectobackend.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserRequestDTO {
    @NotBlank
    public String username;
    @NotBlank
    public String password;
    @NotNull
    public User.Role role;
    public String name;
}
