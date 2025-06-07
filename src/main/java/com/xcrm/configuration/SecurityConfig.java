package com.xcrm.configuration;

import com.xcrm.service.CustomUserDetailsService;
import com.xcrm.utils.CustomRoutingDataSource;
import com.xcrm.utils.LoginRateLimitingFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Servicios y filtros usados para gestionar la seguridad y autenticación
    private CustomUserDetailsService customUserDetailsService;
    private CustomRoutingDataSource customRoutingDataSource;
    private LoginRateLimitingFilter loginRateLimitingFilter;

    /**
     * Configura las reglas y filtros de seguridad para la aplicación.
     * Define qué rutas son públicas, cuáles requieren roles específicos, y cómo se manejan
     * el inicio de sesión y cierre de sesión. También incluye un filtro para limitar
     * intentos fallidos de login y desactiva protección CSRF en ciertas APIs.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .addFilterBefore(loginRateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/",
                                "/caracteristicas",
                                "/precios",
                                "/contacto",
                                "/enviar-contacto",
                                "/registro",
                                "/css/**",
                                "/JavaScript/**",
                                "/images/**",
                                "/aviso-legal")
                        .permitAll()
                        .requestMatchers("/api/cache/clear").hasRole("ADMIN")
                        .requestMatchers("/api/config/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/mi-cuenta",true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .addLogoutHandler((request, response, authentication) -> {
                            customRoutingDataSource.resetToDefaultDataSource();
                        })
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**", "/report/**")
                ).build();
    }

    /**
     * Provee un codificador de contraseñas para almacenar las contraseñas de forma segura.
     * Utiliza el algoritmo BCrypt, que es estándar y seguro para hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura el manejador de autenticación que válida usuarios usando el servicio de detalles de usuario
     * y el codificador de contraseñas definido.
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}