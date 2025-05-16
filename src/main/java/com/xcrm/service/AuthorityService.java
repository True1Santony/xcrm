package com.xcrm.service;

import com.xcrm.model.Authority;
import com.xcrm.model.User;
import com.xcrm.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    // Método para eliminar las autoridades de un usuario
    public void eliminarAutoridades(User usuario) {
        for (Authority authority : usuario.getAuthorities()) {
            authorityRepository.delete(authority);
        }
    }

    // Método para añadir nuevas autoridades
    public void asignarNuevasAutoridades(User usuario, String[] roles) {
        Set<Authority> authorities = new HashSet<>();
        for (String rol : roles) {
            Authority authority = new Authority(UUID.randomUUID(), usuario, rol);
            authorities.add(authority);
        }
        usuario.setAuthorities(authorities);
    }
}