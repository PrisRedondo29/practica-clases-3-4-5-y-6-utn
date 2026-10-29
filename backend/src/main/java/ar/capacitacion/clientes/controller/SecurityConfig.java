package ar.capacitacion.clientes.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/** Seguridad: Resource Server con JWT de Keycloak. Solo SUPERVISOR aprueba solicitudes. */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())   // reutiliza CorsConfig (WebMvcConfigurer)
            .csrf(csrf -> csrf.disable())      // API stateless con Bearer token, sin cookies de sesión
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/solicitudes/{id}/aprobar").hasRole("SUPERVISOR")
                .anyRequest().permitAll())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    /** Convierte realm_access.roles de Keycloak en authorities ROLE_<rol>. */
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            if (jwt.getClaimAsMap("realm_access") != null
                    && jwt.getClaimAsMap("realm_access").get("roles") instanceof Collection<?> roles) {
                return roles.stream()
                        .map(Object::toString)
                        .<GrantedAuthority>map(rol -> new SimpleGrantedAuthority("ROLE_" + rol))
                        .toList();
            }
            return List.<GrantedAuthority>of();
        });
        return converter;
    }
}