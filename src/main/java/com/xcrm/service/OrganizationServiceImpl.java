package com.xcrm.service;

import java.util.List;
import java.util.Optional;

import com.xcrm.model.Organization;
import com.xcrm.model.User;
import com.xcrm.repository.DatabaseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.xcrm.repository.OrganizationRepository;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class OrganizationServiceImpl implements OrganizationService {

	private final OrganizationRepository organizationRepository;
	private final DatabaseRepository databaseRepository;
	private final UserService userService;

	@Override
	public List<Organization> obtenerTodasLasOrganizaciones(){
		return organizationRepository.findAll();
	}

	@Override
	public Optional<Organization> findById(Long id){
		return organizationRepository.findById(id);
	}

	@Override
	public Optional<Organization> findByEmail(String email) {
		return organizationRepository.findByEmail(email);
	}

	@Override
	@Transactional
	public Organization crearOrganizacion(String nombre, String email, String plan) {
		if(organizationRepository.findByNombre(nombre).isPresent()){
			throw new IllegalArgumentException("La organización, con este nombre, ya existe.");
		}
		// Crear una nueva instancia de Organizacion
		Organization nuevaOrganization = new Organization();
		nuevaOrganization.setId(organizationRepository.count()+40);
		nuevaOrganization.setNombre(nombre);
		nuevaOrganization.setEmail(email);
		nuevaOrganization.setPlan(plan);
		nuevaOrganization.setNombreDB(creaNombreBDporNombreEmpresa(nombre));

		//Creacion de la base de datos de la organizacion
		databaseRepository.createDatabase(nuevaOrganization.getNombreDB());
		databaseRepository.createTables(nuevaOrganization.getNombreDB());

		// Insertar la organización en la base de datos de la organización creada
		databaseRepository.insertarOrganizacionEnBaseDeDatos(nombre, nuevaOrganization.getId(),email,plan, nuevaOrganization.getNombreDB());

		// Guardar la organización en la base de datos central.
		System.out.println("Se va a guardar la organización en la base de datos central");
		return organizationRepository.save(nuevaOrganization);
	}

	private String creaNombreBDporNombreEmpresa(String nombreEmpresa){
		String nombreDB = "xcrm_"+nombreEmpresa.toLowerCase().replaceAll("\\s+", "_");
		return nombreDB;
	}

	@Override
    public Organization getCurrentOrganization() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		User user = userService.findByUsername(username);
		return findById(user.getOrganizacion().getId())
				.orElseThrow(()-> new EntityNotFoundException("Organización no encontrada"));
    }

	@Override
	public Optional<Organization> findByNombre(String nombreEmpresa) {
		return organizationRepository.findByNombre(nombreEmpresa);
	}

	@Override
	public void save(Organization organization) {
		organizationRepository.save(organization);
	}
}