package com.xcrm.service;

import com.xcrm.model.Organization;
import com.xcrm.model.User;
import com.xcrm.repository.DatabaseRepository;
import com.xcrm.repository.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class OrganizationServiceImplTest {
    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private DatabaseRepository databaseRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

    @Test
    void obtenerTodasLasOrganizaciones_retornaLista() {
        List<Organization> orgs = List.of(new Organization(1L,"Org1","email1@x.com","basic","db1"),
                new Organization(2L,"Org2","email2@x.com","pro","db2"));
        when(organizationRepository.findAll()).thenReturn(orgs);

        List<Organization> resultado = organizationService.obtenerTodasLasOrganizaciones();

        assertEquals(2, resultado.size());
        verify(organizationRepository).findAll();
    }

    @Test
    void crearOrganizacion_exitoso_guardaYCreaBD() {
        String nombre = "EmpresaTest";
        String email = "test@empresa.com";
        String plan = "basic";

        when(organizationRepository.findByNombre(nombre)).thenReturn(Optional.empty());
        when(organizationRepository.count()).thenReturn(10L);

        ArgumentCaptor<Organization> captorOrg = ArgumentCaptor.forClass(Organization.class);
        when(organizationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Organization created = organizationService.crearOrganizacion(nombre, email, plan);

        // Verificar que se haya llamado el repositorio para crear BD y tablas
        verify(databaseRepository).createDatabase(created.getNombreDB());
        verify(databaseRepository).createTables(created.getNombreDB());

        // Verificar inserción organización en BD organización
        verify(databaseRepository).insertarOrganizacionEnBaseDeDatos(
                eq(nombre), eq(created.getId()), eq(email), eq(plan), eq(created.getNombreDB()));

        // Verificar guardado final en repo central
        verify(organizationRepository).save(captorOrg.capture());
        Organization savedOrg = captorOrg.getValue();

        assertEquals(nombre.toLowerCase().replaceAll("\\s+", "_"), savedOrg.getNombreDB().substring(5)); // "xcrm_"
        assertEquals(nombre, savedOrg.getNombre());
        assertEquals(email, savedOrg.getEmail());
        assertEquals(plan, savedOrg.getPlan());
        assertNotNull(savedOrg.getCreado());
    }

    @Test
    void crearOrganizacion_nombreExistente_lanzaExcepcion() {
        when(organizationRepository.findByNombre("OrgExistente")).thenReturn(Optional.of(new Organization()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> organizationService.crearOrganizacion("OrgExistente", "email@x.com", "basic"));

        assertEquals("La organización, con este nombre, ya existe.", ex.getMessage());
    }

    @Test
    void getCurrentOrganization_usuarioExistente_retornaOrganizacion() {
        // Mock SecurityContext
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("usuario1");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(securityContext);

        User mockUser = new User();
        Organization org = new Organization(99L,"OrgMock","mock@org.com","planX","db_mock");
        mockUser.setOrganizacion(org);

        when(userService.findByUsername("usuario1")).thenReturn(mockUser);
        when(organizationRepository.findById(org.getId())).thenReturn(Optional.of(org));

        Organization resultado = organizationService.getCurrentOrganization();

        assertEquals(org, resultado);
    }
/*
    @Test
    void getCurrentOrganization_usuarioSinOrganizacion_lanzaEntityNotFoundException() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("usuarioSinOrg");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(securityContext);

        User mockUser = new User();
        mockUser.setOrganizacion(null);

        when(userService.findByUsername("usuarioSinOrg")).thenReturn(mockUser);

        assertThrows(EntityNotFoundException.class,
                () -> organizationService.getCurrentOrganization());
    }
*/
}
