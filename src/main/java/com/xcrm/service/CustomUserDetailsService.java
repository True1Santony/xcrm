package com.xcrm.service;

import com.xcrm.model.Authority;
import com.xcrm.model.User;
import com.xcrm.repository.AuthorityRepository;
import com.xcrm.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.xcrm.configuration.DataSourceConfig;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private DataSourceConfig dataSourceConfig;

    @Autowired
    HttpSession httpSession;

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        //obtengo las autorizacionces del usuario
        List<Authority> authorities = authorityRepository.findByUser_Username(username);

        // Loguear los roles del usuario
        logger.info("Roles para el usuario {}: {}", username, authorities.stream()
                .map(Authority::getAuthority)
                .collect(Collectors.joining(", ")));

        // Convertir a la estructura esperada por Spring Security
        List<GrantedAuthority> grantedAuthorities = authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList());

        // Establecer el nombre de la organización en el contexto
        String nombreDB = user.getOrganizacion().getNombreDB();

        //establece el nombre de la DB para cambiar el datasource de la organizacion en curso
        httpSession.setAttribute("TENANT_ID", nombreDB);

        // Construir el objeto UserDetails a partir del objeto User
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(grantedAuthorities) // Asignar las autoridades
                .disabled(!user.isEnabled()) // Controlar si el usuario está habilitado
                .build();
    }

}
