package com.xcrm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xcrm.model.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

	//Genera una query con where nombre = ?, Spring Data JPA lo convierte automaticamente en una consulta
	Optional<Organization> findByNombre(String nombre);


    Optional<Organization> findByEmail(String email);
}