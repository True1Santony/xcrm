package com.xcrm.service;

import com.xcrm.model.*;
import com.xcrm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
        nuevoUsuario.setId(UUID.randomUUID());
        nuevoUsuario.setUsername(username); // Establecer el nombre de usuario
        nuevoUsuario.setPassword(passwordEncoder.encode(rawPassword));
        nuevoUsuario.setEnabled(true); // Habilitar el usuario
        nuevoUsuario.setOrganizacion(organizacion); // Establecer la relación con la organización

        // Guardar el usuario en la base de datos
        userRepository.save(nuevoUsuario);

        Authority authority = new Authority();
        authority.setId(UUID.randomUUID());
        authority.setUser(nuevoUsuario);
        authority.setAuthority(role);

        authorityRepository.save(authority);

        // Insertar el usuario en la base de datos de la organización (String dbName, UUID userId, Long organizacionId, String username, String password)
        databaseRepository.insertarUsuarioEnBaseDeDatos(organizacion.getNombreDB(),nuevoUsuario.getId(),organizacion.getId(),nuevoUsuario.getUsername(), nuevoUsuario.getPassword());

        // Insertar el rol de administrador para el usuario (String dbName, UUID idAuthority, UUID userId, String role)
        databaseRepository.insertarRolDeUsuarioEnBaseDeDatos(organizacion.getNombreDB(), authority.getId(),nuevoUsuario.getId(), role);

    }

    @Transactional
    public void createUserInOrganization(String userName, String rawPassword, Organizacion organizacion){

        // Verificar si el usuario ya existe
        if (userRepository.findByUsername(userName) != null) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        // Crear una nueva instancia de User
        User nuevoUsuario = new User();
        nuevoUsuario.setId(UUID.randomUUID());
        nuevoUsuario.setUsername(userName);
        nuevoUsuario.setPassword(passwordEncoder.encode(rawPassword));
        nuevoUsuario.setEnabled(true); // Habilitar el usuario
        nuevoUsuario.setOrganizacion(organizacion); // Establecer la relación con la organización

        // Guardar el usuario en la base de datos
        userRepository.save(nuevoUsuario);

        // Crear y guardar la autoridad
        Authority authority = new Authority();
        authority.setId(UUID.randomUUID());
        authority.setAuthority("ROLE_USER");
        authority.setUser(nuevoUsuario);

        authorityRepository.save(authority);

        // Agregar la autoridad al usuario
        nuevoUsuario.getAuthorities().add(authority);
        userRepository.save(nuevoUsuario); // Actualizar el usuario con la autoridad

        byte[] uuidUserIdBytes =  uuidToBytes(nuevoUsuario.getId());
        // Guardo al usuario en la base de datos central
        String insertUserQuery = "INSERT INTO mi_app.users (id, username, password, enabled, organizacion_id) VALUES (?,?, ?, ?, ?)";
        jdbcTemplate.update(insertUserQuery, uuidUserIdBytes, userName, nuevoUsuario.getPassword(), nuevoUsuario.isEnabled(), organizacion.getId());

        byte[] uuidAuthorityIdBytes = uuidToBytes(authority.getId());
        //Los roles del User en la base de datos central
        String insertAuthorityQuery = "INSERT INTO mi_app.authorities (id,authority, user_id) VALUES (?,?,?)";
        jdbcTemplate.update(insertAuthorityQuery, uuidAuthorityIdBytes, "ROLE_USER", uuidUserIdBytes);

    }

    @Transactional
    public void actualizarUsuario(UUID userId, String nuevoUsername, String nuevoPassword, String[] roles) {
        User usuario = find(userId);

        if (nuevoUsername != null && !nuevoUsername.isEmpty() && !nuevoUsername.equals(usuario.getUsername())) {
            usuario.setUsername(nuevoUsername);
        }

        if (nuevoPassword != null && !nuevoPassword.isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(nuevoPassword));
        }

        authorityRepository.deleteAll(usuario.getAuthorities());
        usuario.getAuthorities().clear();

        Set<Authority> nuevasAuthorities = Arrays.stream(roles)
                .map(rol -> new Authority(UUID.randomUUID(), usuario, rol))
                .collect(Collectors.toSet());

        usuario.setAuthorities(nuevasAuthorities);

        save(usuario);//guardo en la base de datos de la organizacion

        //actualizar en la base de datos central(para que se logee correctamente)
        databaseRepository.actualizarUsuarioYRolesEnDbCentral(
                usuario.getId(),
                usuario.getUsername(),
                nuevoPassword,
                nuevasAuthorities
        );
    }


    public User obtenerUsuarioPorNombre(String nombre) {
        return userRepository.findByUsername(nombre);
    }



    public void save(User user){
        userRepository.save(user);
    }

    public User find(UUID id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElse(null); // Devuelve null si no se encuentra el usuario
    }

    // Método para convertir UUID a byte[]
    private byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    @Transactional
    public void eliminarUsuario(UUID userId) {
        User usuario = find(userId);

        //1. elimina de la base de datos actual de la organizacion
        authorityRepository.deleteAll(usuario.getAuthorities());
        userRepository.delete(usuario);

        //2. elimina de la base de datos central
        databaseRepository.eliminarUsuarioYRolesDeDbCentral(userId);
    }
}

