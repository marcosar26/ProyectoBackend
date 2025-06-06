package es.marcosar.proyectobackend.dto;

import java.util.List;

public class JwtResponse {
    public String token;
    public String type = "Bearer";
    public Long id; // Opcional: enviar ID del usuario
    public String username;
    public List<String> roles;

    public JwtResponse(String accessToken, Long id, String username, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.roles = roles;
    }
}
