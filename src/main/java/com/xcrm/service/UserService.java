package com.xcrm.service;

import com.xcrm.model.Authority;
import com.xcrm.model.User;
import com.xcrm.model.Organizacion;
import com.xcrm.repository.AuthorityRepository;
import com.xcrm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * descomponer para el principio de responsabilidad unica....
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository; // Inyectar el repositorio de usuarios
    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Para encriptar contraseñas

    @Autowired
    private JdbcTemplate jdbcTemplate; // Inyectar JdbcTemplate

    @Transactional
    public void crearUsuarioConOrganizacion(String username, String rawPassword, Organizacion organizacion, String role) {

        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        // Crear una nueva instancia de User
        User nuevoUsuario = new User();
        nuevoUsuario.setUsername(username); // Establecer el nombre de usuario
        nuevoUsuario.setPassword(passwordEncoder.encode(rawPassword)); // Encriptar la contraseña
        nuevoUsuario.setEnabled(true); // Habilitar el usuario
        nuevoUsuario.setOrganizacion(organizacion); // Establecer la relación con la organización

        // Guardar el usuario en la base de datos
        userRepository.save(nuevoUsuario);

        Authority authority = new Authority();
        authority.setUsername(username);
        authority.setAuthority(role);

        authorityRepository.save(authority);
    }

    @Transactional
    public void crearNuevoUsuario(String userName, String rawPassword, Organizacion organizacion){

        // Verificar si el usuario ya existe
        if (userRepository.findByUsername(userName) != null) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        // Crear una nueva instancia de User
        User nuevoUsuario = new User();
        nuevoUsuario.setUsername(userName);
        nuevoUsuario.setPassword(passwordEncoder.encode(rawPassword));
        nuevoUsuario.setEnabled(true); // Habilitar el usuario
        nuevoUsuario.setOrganizacion(organizacion); // Establecer la relación con la organización

        // Guardar el usuario en la base de datos
        userRepository.save(nuevoUsuario);

        // Crear y guardar la autoridad
        Authority authority = new Authority();
        authority.setUsername(userName);
        authority.setAuthority("ROLE_USER");

        authorityRepository.save(authority);

        // Agregar la autoridad al usuario
        nuevoUsuario.getAuthorities().add(authority);
        userRepository.save(nuevoUsuario); // Actualizar el usuario con la autoridad

        // Ahora guardar el usuario en la base de datos central
        String insertUserQuery = "INSERT INTO mi_app.users (username, password, enabled, organizacion_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(insertUserQuery, userName, nuevoUsuario.getPassword(), nuevoUsuario.isEnabled(), organizacion.getId());

        // Si necesitas también agregar la autoridad en la base de datos central, hazlo aquí
        String insertAuthorityQuery = "INSERT INTO mi_app.authorities (username, authority) VALUES (?, ?)";
        jdbcTemplate.update(insertAuthorityQuery, userName, "ROLE_USER");

    }


}

