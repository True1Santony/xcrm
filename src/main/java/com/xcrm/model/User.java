package com.xcrm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Column
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    private boolean enabled;

    @ManyToOne
    @JoinColumn(name = "organizacion_id", nullable = false)
    @JsonBackReference // Indica el lado inverso.api
    private Organizacion organizacion;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Authority> authorities = new HashSet<>(); // Relación con Authority

    @ManyToMany
    @JoinTable(
            name = "comerciales_campanias",
            joinColumns = @JoinColumn(name = "comercial_id"),
            inverseJoinColumns = @JoinColumn(name = "campania_id")
    )
    private Set<Campania> campanias = new HashSet<>(); // Relación con las campañas

    @ManyToMany
    @JoinTable(
            name = "comerciales_clientes",
            joinColumns = @JoinColumn(name = "comercial_id"),
            inverseJoinColumns = @JoinColumn(name = "cliente_id")
    )
    private Set<Cliente> clientes = new HashSet<>(); // Relación con los clientes

    // Constructor, getters y setters
    public User() {
    }

    public User(UUID id, String username, String password, boolean enabled, Organizacion organizacion) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.organizacion = organizacion;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Organizacion getOrganizacion() {
        return organizacion;
    }

    public void setOrganizacion(Organizacion organizacion) {
        this.organizacion = organizacion;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public Set<Campania> getCampanias() {
        return campanias;
    }

    public void setCampanias(Set<Campania> campanias) {
        this.campanias = campanias;
    }

    public void addCampania(Campania campania) {
        campanias.add(campania);
        campania.getComerciales().add(this); // Agregar el comercial a la campaña
    }

    public Set<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(Set<Cliente> clientes) {
        this.clientes = clientes;
    }
    public void addCliente(Cliente cliente) {
        clientes.add(cliente);
        cliente.getComerciales().add(this); // Agregar el comercial al cliente
    }
}
