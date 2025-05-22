package es.marcosar.proyectobackend.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = jwtUtil.getTokenFromHeader(request);
            if (jwt != null && jwtUtil.validateJwtToken(jwt)) {
                String username = jwtUtil.getUsernameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null, // No se necesitan credenciales (password) aquí
                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            // En AuthTokenFilter.java, dentro de doFilterInternal
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {} for request URI: {}", e.getMessage(), request.getRequestURI());
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Podrías manejar el error aquí directamente
            // return; // Y no continuar el filterChain si el token está expirado
        } catch (SignatureException e) { // Específicamente para problemas de firma
            logger.error("JWT signature validation failed: {} for request URI: {}", e.getMessage(), request.getRequestURI());
        } catch (Exception e) { // Catch general
            logger.error("No se pudo establecer la autenticación del usuario (General Exception): {} for request URI: {}", e.getMessage(), request.getRequestURI(), e);
        }

        filterChain.doFilter(request, response);
    }
}