package com.xcrm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xcrm.model.Organizacion;

@Repository
public interface OrganizacionRepository extends JpaRepository<Organizacion, Long> {

	//Genera una query con where nombre = ?, Spring Data JPA lo convierte automaticamente en una consulta
	Optional<Organizacion> findByNombre(String nombre);


    Optional<Organizacion> findByEmail(String email);
}
