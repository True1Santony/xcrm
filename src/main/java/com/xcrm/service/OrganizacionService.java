package com.xcrm.service;

import java.util.List;
import java.util.Optional;

import com.xcrm.model.User;
import com.xcrm.repository.DatabaseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.xcrm.model.Organizacion;
import com.xcrm.repository.OrganizacionRepository;

@Service
public class OrganizacionService {

	@Autowired
	private OrganizacionRepository organizacionRepository;

	@Autowired
	private DatabaseRepository databaseRepository;

	@Autowired
	UserService userService;

	public List<Organizacion> obtenerTodasLasOrganizaciones(){
		return organizacionRepository.findAll();
	}
	
	public Optional<Organizacion> findById(Long id){
		return organizacionRepository.findById(id);
	}

	public Optional<Organizacion> buscarPorEmail(String email) {
		return organizacionRepository.findByEmail(email);
	}

	public Organizacion crearOrganizacion(String nombre, String email, String plan) {
		if(organizacionRepository.findByNombre(nombre).isPresent()){
			throw new IllegalArgumentException("La organización, con este nombre, ya existe.");
		}
		// Crear una nueva instancia de Organizacion
		Organizacion nuevaOrganizacion = new Organizacion();
		nuevaOrganizacion.setId(organizacionRepository.count()+30);
		nuevaOrganizacion.setNombre(nombre);
		nuevaOrganizacion.setEmail(email);
		nuevaOrganizacion.setPlan(plan);
		nuevaOrganizacion.setNombreDB(creaNombreBDporNombreEmpresa(nombre));

		//Creacion de la base de datos de la organizacion
		databaseRepository.createDatabase(nuevaOrganizacion.getNombreDB());
		databaseRepository.createTables(nuevaOrganizacion.getNombreDB());

		// Insertar la organización en la base de datos de la organización
		databaseRepository.insertarOrganizacionEnBaseDeDatos(nombre,nuevaOrganizacion.getId(),email,plan, nuevaOrganizacion.getNombreDB());

		// Guardar la organización
		System.out.println("Se va a guardar la organización");
		return organizacionRepository.save(nuevaOrganizacion);
	}

	private String creaNombreBDporNombreEmpresa(String nombreEmpresa){
		String nombreDB = "xcrm_"+nombreEmpresa.toLowerCase().replaceAll("\\s+", "_");
		return nombreDB;
	}

    public Organizacion getOrganizacionActual() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		User user = userService.obtenerUsuarioPorNombre(username);
		return findById(user.getOrganizacion().getId())
				.orElseThrow(()-> new EntityNotFoundException("Organización no encontrada"));
    }
}
