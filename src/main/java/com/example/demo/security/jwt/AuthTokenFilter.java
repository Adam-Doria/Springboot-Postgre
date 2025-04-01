package com.example.demo.security.jwt;

import com.example.demo.security.services.UserDetailsServiceImpl;
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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre pour intercepter les requêtes et valider les tokens JWT
 */
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    @Autowired
    private JwtProperties jwtProperties;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    /**
     * Intercepte chaque requête pour valider le token JWT
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            
            // Si le token est présent et valide
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // Extrait le nom d'utilisateur du token
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // Charge les détails de l'utilisateur
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Crée un token d'authentification pour Spring Security
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                
                // Ajoute des détails à l'authentification
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Définit l'authentification dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Impossible de définir l'authentification utilisateur: {}", e.getMessage());
        }

        // Continue la chaîne de filtres
        filterChain.doFilter(request, response);
    }

    /**
     * Extrait le token JWT de la requête
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(jwtProperties.getHeaderString());

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(jwtProperties.getTokenPrefix())) {
            // Supprime le préfixe (ex: "Bearer ")
            return headerAuth.substring(jwtProperties.getTokenPrefix().length());
        }

        return null;
    }
} 