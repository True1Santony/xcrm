package com.xcrm.service;

import com.xcrm.model.Authority;
import com.xcrm.model.User;
import com.xcrm.repository.AuthorityRepository;
import com.xcrm.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        //obtengo las autorizacionces del usuario
        List<Authority> authorities = authorityRepository.findByUser_Username(username);

        // Imprimir los roles que se están recuperando
        System.out.println("Roles para el usuario " + username + ": " + authorities.stream()
                .map(Authority::getAuthority)
                .collect(Collectors.joining(", ")));

        // Convertir a la estructura esperada por Spring Security
        List<GrantedAuthority> grantedAuthorities = authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList());


        // Establecer el nombre de la organización en el contexto
        String orgName = user.getOrganizacion().getNombre();
        //UserContextHolder.setCurrentOrgName(orgName);

        //cambio de datasource de la organizacion a la que pertenece el user
        //updateDataSource(orgName);


        httpSession.setAttribute("TENANT_ID", orgName);

        // Construir el objeto UserDetails a partir del objeto User
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(grantedAuthorities) // Asignar las autoridades
                .disabled(!user.isEnabled()) // Controlar si el usuario está habilitado
                .build();
    }

    /*private void updateDataSource(String orgName) {
        // Cambia la URL del DataSource para apuntar a la base de datos de la organización
        DataSource routingDataSource = dataSourceConfig.routingDataSource();
        DataSource newDataSource = dataSourceConfig.getDataSource(orgName); // Obtener el nuevo DataSource


        // Verificar si es una instancia de DriverManagerDataSource para obtener la URL de conexión
        if (newDataSource instanceof DriverManagerDataSource) {
            DriverManagerDataSource driverManagerDataSource = (DriverManagerDataSource) newDataSource;
            System.out.println("Conectando a la base de datos para " + orgName + ": " + driverManagerDataSource.getUrl());
        }

        // Puedes establecer el nuevo DataSource en el RoutingDataSource
        // Este método puede variar según tu implementación de RoutingDataSource
        if (routingDataSource instanceof CustomRoutingDataSource) {
            ((CustomRoutingDataSource) routingDataSource).setTargetDataSources(Map.of(orgName, newDataSource));
        }
    }*/


}
