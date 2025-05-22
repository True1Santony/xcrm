package com.xcrm.service;

import com.xcrm.model.Authority;
import com.xcrm.model.Organization;
import com.xcrm.model.User;
import com.xcrm.repository.DatabaseRepository;
import com.xcrm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorityService authorityService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private DatabaseRepository databaseRepository;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<Authority> authorityCaptor;

    private Organization mockOrg;

    @BeforeEach
    void setup() {
        mockOrg = new Organization();
        mockOrg.setId(1L);
        mockOrg.setNombreDB("test_db");
        userService = new UserServiceImpl(
                userRepository,
                authorityService,
                passwordEncoder,
                jdbcTemplate,
                databaseRepository,
                "central_db"
        );
    }

    @Test
    void crearUsuarioConOrganizacion_usuarioExistente_lanzaException() {
        when(userRepository.findByUsername("existingUser")).thenReturn(new User());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                userService.crearUsuarioConOrganizacion("existingUser", "pass", mockOrg, "ROLE_ADMIN"));

        assertEquals("El usuario ya existe", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void crearUsuarioConOrganizacion_usuarioNoExiste_creaUsuarioYAutoridad() {
        when(userRepository.findByUsername("newUser")).thenReturn(null);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        userService.crearUsuarioConOrganizacion("newUser", "pass", mockOrg, "ROLE_ADMIN");

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("newUser", savedUser.getUsername());
        assertEquals("encodedPass", savedUser.getPassword());
        assertTrue(savedUser.isEnabled());
        assertEquals(mockOrg, savedUser.getOrganizacion());

        verify(authorityService).save(authorityCaptor.capture());
        Authority savedAuth = authorityCaptor.getValue();
        assertEquals("ROLE_ADMIN", savedAuth.getAuthority());
        assertEquals(savedUser, savedAuth.getUser());

        verify(databaseRepository).insertarUsuarioEnBaseDeDatos(eq("test_db"), eq(savedUser.getId()), eq(1L), eq("newUser"), eq("encodedPass"));
        verify(databaseRepository).insertarRolDeUsuarioEnBaseDeDatos(eq("test_db"), eq(savedAuth.getId()), eq(savedUser.getId()), eq("ROLE_ADMIN"));
    }

    @Test
    void createUserInOrganization_usuarioExistente_lanzaException() {
        when(userRepository.findByUsername("existingUser")).thenReturn(new User());

        assertThrows(IllegalArgumentException.class, () ->
                userService.createUserInOrganization("existingUser", "pass", mockOrg));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUserInOrganization_usuarioNuevo_guardaUsuarioYAutoridadYCentrales() {
        when(userRepository.findByUsername("newUser")).thenReturn(null);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        userService.createUserInOrganization("newUser", "pass", mockOrg);

        verify(userRepository, times(2)).save(userCaptor.capture()); // guarda dos veces: creación y actualización authorities
        List<User> savedUsers = userCaptor.getAllValues();

        User firstSave = savedUsers.get(0);
        User secondSave = savedUsers.get(1);
        assertEquals("newUser", firstSave.getUsername());
        assertEquals("encodedPass", firstSave.getPassword());
        assertTrue(firstSave.isEnabled());
        assertEquals(mockOrg, firstSave.getOrganizacion());

        // Verificar autoridad ROLE_USER agregada
        assertTrue(secondSave.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        // Verificar inserciones en base central con jdbcTemplate
        verify(jdbcTemplate).update(startsWith("INSERT INTO central_db.users"), any(), eq("newUser"), eq("encodedPass"), eq(true), eq(1L));
        verify(jdbcTemplate).update(startsWith("INSERT INTO central_db.authorities"), any(), eq("ROLE_USER"), any());
    }

    @Test
    void actualizarUsuario_cambiaDatosYAutoridades() {
        UUID userId = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("oldUser");
        existingUser.setPassword("oldPass");
        existingUser.setAuthorities(new HashSet<>(Set.of(new Authority(UUID.randomUUID(), existingUser, "ROLE_OLD"))));

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        String[] roles = {"ROLE_ADMIN", "ROLE_USER"};

        userService.actualizarUsuario(userId, "newUser", "newPass", roles);

        // Verifica cambio de username y password
        assertEquals("newUser", existingUser.getUsername());
        assertEquals("encodedNewPass", existingUser.getPassword());

        verify(authorityService).deleteAll(anySet());
        verify(userRepository).save(existingUser);

        verify(databaseRepository).actualizarUsuarioYRolesEnDbCentral(eq(userId), eq("newUser"), eq("encodedNewPass"), anySet());
    }

    @Test
    void eliminarUsuario_eliminaDeBDOrganizacionYCentral() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setAuthorities(Set.of(new Authority()));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.eliminarUsuario(userId);

        verify(authorityService).deleteAll(user.getAuthorities());
        verify(userRepository).delete(user);
        verify(databaseRepository).eliminarUsuarioYRolesDeDbCentral(userId);
    }

    @Test
    void findByUsername_retornaUsuario() {
        User user = new User();
        when(userRepository.findByUsername("user1")).thenReturn(user);

        User result = userService.findByUsername("user1");
        assertEquals(user, result);
    }

    @Test
    void findById_usuarioExistente_retornaUsuario() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.findById(userId);
        assertEquals(user, result);
    }

    @Test
    void findById_usuarioNoExistente_retornaNull() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        User result = userService.findById(userId);
        assertNull(result);
    }

    @Test
    void findAll_retornaLista() {
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAll();
        assertEquals(users, result);
    }
}
