package com.xcrm.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;


@Entity
@Table(name="organizaciones")
public class Organizacion implements Serializable {
	
	@Id
	private Long id;

	@Column(name = "nombreDB", nullable = false, length = 50, unique = true)
	private String nombreDB;
	
	@Column(nullable = false,unique = true, length = 100)
	private String nombre;
	
	@Column(nullable = false, length = 100, unique = true)
	private String email;
	
	@Column(nullable = false, length = 50)
	private String plan;
	
	@Column(name = "creado", nullable = false)
	private LocalDateTime creado;

	public Organizacion() {
		this.creado = LocalDateTime.now();
	}

	@OneToMany(mappedBy = "organizacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonManagedReference // Indica el lado principal para evitar anidamiento al serializar.api
	private Set<User> usuarios = new HashSet<>(); // Una organización puede tener varios usuarios

	@OneToMany(mappedBy = "organizacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonManagedReference // Evitar problemas de serialización
	private Set<Cliente> clientes = new HashSet<>();

	public Organizacion(Long id, String nombre, String email, String plan, String nombreDB) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.email = email;
		this.plan = plan;
		this.nombreDB=nombreDB;
		this.creado = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public LocalDateTime getCreado() {
		return creado;
	}

	public void setCreado(LocalDateTime creado) {
		this.creado = creado;
	}

	public Set<User> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(Set<User> usuarios) {
		this.usuarios = usuarios;
	}

	public void addUsuario(User usuario) {
		usuarios.add(usuario);
		usuario.setOrganizacion(this);
	}

	public String getNombreDB() {
		return nombreDB;
	}

	public void setNombreDB(String nombreDB) {
		this.nombreDB = nombreDB;
	}

	public Set<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(Set<Cliente> clientes) {
		this.clientes = clientes;
	}

	public void addCliente(Cliente cliente) {
		clientes.add(cliente);
		cliente.setOrganizacion(this);
	}
}
