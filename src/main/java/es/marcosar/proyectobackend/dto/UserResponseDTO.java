package es.marcosar.proyectobackend.dto;

import es.marcosar.proyectobackend.entity.User;

public class UserResponseDTO {
    public Long id;
    public String username;
    public User.Role role;
    public String name;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.name = user.getName();
    }
}
