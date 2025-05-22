package com.xcrm.service;

import com.xcrm.model.Organization;
import com.xcrm.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void crearUsuarioConOrganizacion(String username, String rawPassword, Organization organization, String role);
    void createUserInOrganization(String userName, String rawPassword, Organization organization);
    void actualizarUsuario(UUID userId, String nuevoUsername, String nuevoPassword, String[] roles);
    User findByUsername(String nombre);
    void save(User user);
    User findById(UUID id);
    void eliminarUsuario(UUID userId);
    List<User> findAll();
}
