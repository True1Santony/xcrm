package com.xcrm.service;

import com.xcrm.model.Organization;

import java.util.List;
import java.util.Optional;

public interface OrganizationService {
    List<Organization> obtenerTodasLasOrganizaciones();
    Optional<Organization> findById(Long id);
    Optional<Organization> findByEmail(String email);
    Organization crearOrganizacion(String nombre, String email, String plan);
    Organization getCurrentOrganization();
    Optional<Organization> findByNombre(String nombreEmpresa);
}
