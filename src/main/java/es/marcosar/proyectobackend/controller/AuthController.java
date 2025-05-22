package es.marcosar.proyectobackend.controller;

import es.marcosar.proyectobackend.dto.JwtResponse;
import es.marcosar.proyectobackend.dto.LoginRequest;
import es.marcosar.proyectobackend.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
// @CrossOrigin(origins = "http://localhost:4200", maxAge = 3600) // Ya configurado globalmente en WebConfig
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    es.marcosar.proyectobackend.repository.UserRepository userRepository; // Para obtener el ID del usuario

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateJwtToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Obtener el ID del usuario desde la base de datos
        // Es mejor obtenerlo del UserDetails si lo personalizas para incluir el ID,
        // o hacer una búsqueda aquí.
        Long userId = userRepository.findByUsername(userDetails.getUsername())
                .map(es.marcosar.proyectobackend.entity.User::getId)
                .orElse(null); // Manejar si no se encuentra, aunque no debería pasar si la autenticación fue exitosa.


        return ResponseEntity.ok(new JwtResponse(jwt,
                userId,
                userDetails.getUsername(),
                roles));
    }

    // (Opcional) Endpoint de Registro - necesitaría hashear contraseña antes de guardar
    /*
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // Lógica para registrar usuario:
        // 1. Verificar si el username ya existe
        // 2. Hashear la contraseña
        // 3. Crear y guardar el nuevo User
        // 4. Devolver una respuesta
        return ResponseEntity.ok("Usuario registrado exitosamente!");
    }
    */
}