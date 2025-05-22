package es.marcosar.proyectobackend.security;

import es.marcosar.proyectobackend.entity.User;
import es.marcosar.proyectobackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));

        // El rol en la entidad User es User.Role (enum). Spring Security espera "ROLE_ADMIN", "ROLE_MANAGER", etc.
        // o simplemente "ADMIN", "MANAGER", "USER" si configuras el `GrantedAuthorityDefaults` o manejas los prefijos.
        // Aquí usaremos el nombre del enum directamente.
        Set<GrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority(user.getRole().name()) // ej. "ADMIN", "MANAGER", "USER"
        );
        // Alternativamente, si quieres el prefijo ROLE_:
        // Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));


        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // Spring Security usará esto para comparar con la contraseña hasheada
                authorities);
    }
}