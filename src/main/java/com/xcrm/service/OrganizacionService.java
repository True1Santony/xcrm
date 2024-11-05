package com.xcrm.service;

import java.util.List;
import java.util.Optional;

import com.xcrm.repository.DatabaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xcrm.model.Organizacion;
import com.xcrm.repository.OrganizacionRepository;

@Service
public class OrganizacionService {

	@Autowired
	private OrganizacionRepository organizacionRepository;

	@Autowired
	private DatabaseRepository databaseRepository;
	
	
	public List<Organizacion> obtenerTodasLasOrganizaciones(){
		return organizacionRepository.findAll();
	}
	
	public Optional<Organizacion> obtenerPorId(Long id){
		return organizacionRepository.findById(id);
	}
	
	public Organizacion crearOrganizacion(Organizacion organizacion) {
		System.out.println("Se va a guardar la organizacion");
        return organizacionRepository.save(organizacion);
    }
	
	 public Organizacion actualizarOrganizacion(Long id, Organizacion organizacionActualizada) {
	        organizacionActualizada.setId(id);
	        return organizacionRepository.save(organizacionActualizada);
	    }
	
	 public void eliminarOrganizacion(Long id) {
	        
	        organizacionRepository.deleteById(id);
	    }
	 
	 public List<Organizacion> buscarPorNombre(String nombre) {
	        return organizacionRepository.findByNombre(nombre); 
	    }

	public Optional<Organizacion> buscarPorEmail(String email) {
		return organizacionRepository.findByEmail(email);
	}

	/**
	 * Crea la organizacion y su base de datos
	 * @param nombre
	 * @param email
	 * @param plan
	 * @return
	 */
	public Organizacion crearOrganizacion(String nombre, String email, String plan) {
		// Crear una nueva instancia de Organizacion
		Organizacion nuevaOrganizacion = new Organizacion();
		nuevaOrganizacion.setNombre(nombre);
		nuevaOrganizacion.setEmail(email);
		nuevaOrganizacion.setPlan(plan);

		//Creacion de la base de datos de la organizacion

		databaseRepository.createDatabase(nombre);

		// Guardar la organización
		System.out.println("Se va a guardar la organización");
		return organizacionRepository.save(nuevaOrganizacion);
	}

	public Organizacion buscarPorId(Long organizacionId) {
        return organizacionRepository.getById(organizacionId);
    }

	public String obtenerDatabaseUrlPorNombre(String nombre) {
		Optional<Organizacion> organizacion = organizacionRepository.findByNombre(nombre).stream().findFirst();
		if (organizacion.isPresent()) {
			return "jdbc:mysql://localhost:3306/" + organizacion.get().getNombre(); // Ajusta esta URL según tu configuración.
		} else {
			throw new IllegalArgumentException("Organización no encontrada: " + nombre);
		}
	}
}
