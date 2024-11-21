package com.xcrm.service;

import com.xcrm.model.*;
import com.xcrm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CampaniaRepository campaniaRepository;

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

        // Insertar el usuario en la base de datos de la organización
        databaseRepository.insertarUsuarioEnBaseDeDatos(organizacion.getNombreDB(),organizacion.getId(),username, nuevoUsuario.getPassword());

        // Insertar el rol de administrador para el usuario
        databaseRepository.insertarRolDeUsuarioEnBaseDeDatos(organizacion.getNombreDB(), username, role);
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

        // Guardo al usuario en la base de datos central
        String insertUserQuery = "INSERT INTO mi_app.users (username, password, enabled, organizacion_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(insertUserQuery, userName, nuevoUsuario.getPassword(), nuevoUsuario.isEnabled(), organizacion.getId());

        //Los roles del User en la base de datos central
        String insertAuthorityQuery = "INSERT INTO mi_app.authorities (username, authority) VALUES (?, ?)";
        jdbcTemplate.update(insertAuthorityQuery, userName, "ROLE_USER");

    }

    public User obtenerUsuarioPorNombre(String nombre) {
        return userRepository.findByUsername(nombre);
    }

    // Asignar un cliente a un comercial
    @Transactional
    public void addClienteToComercial(String username, Long clienteId) {
        Optional<User> userOpt = userRepository.findById(username);
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);

        if (userOpt.isPresent() && clienteOpt.isPresent()) {
            User user = userOpt.get();
            Cliente cliente = clienteOpt.get();
            user.addCliente(cliente);
            userRepository.save(user); // Guardamos al comercial con la relación actualizada
            clienteRepository.save(cliente); // Guardamos el cliente con la relación actualizada
        }
    }

    // Asignar una campaña a un comercial
    @Transactional
    public void addCampaniaToComercial(String username, Long campaniaId) {
        Optional<User> userOpt = userRepository.findById(username);
        Optional<Campania> campaniaOpt = campaniaRepository.findById(campaniaId);

        if (userOpt.isPresent() && campaniaOpt.isPresent()) {
            User user = userOpt.get();
            Campania campania = campaniaOpt.get();
            user.addCampania(campania);
            userRepository.save(user); // Guardamos al comercial con la relación actualizada
        }
    }

}

