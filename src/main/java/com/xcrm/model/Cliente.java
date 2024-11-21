package com.xcrm.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clientes")
public class Cliente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String email;

    private String telefono;

    private String direccion;

    @ManyToOne
    @JoinColumn(name = "organizacion_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_cliente_organizacion"))
    private Organizacion organizacion;

    @ManyToMany
    @JoinTable(
            name = "clientes_campanias",
            joinColumns = @JoinColumn(name = "cliente_id"),
            inverseJoinColumns = @JoinColumn(name = "campania_id")
    )
    private Set<Campania> campanias = new HashSet<>(); // Relación con las campañas

    @ManyToMany(mappedBy = "clientes")
    private Set<User> comerciales = new HashSet<>(); // Relación con los comerciales

    public Set<User> getComerciales() {
        return comerciales;
    }

    public void setComerciales(Set<User> comerciales) {
        this.comerciales = comerciales;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Organizacion getOrganizacion() {
        return organizacion;
    }

    public void setOrganizacion(Organizacion organizacion) {
        this.organizacion = organizacion;
    }

    public Set<Campania> getCampanias() {
        return campanias;
    }

    public void setCampanias(Set<Campania> campanias) {
        this.campanias = campanias;
    }

    public void addCampania(Campania campania) {
        campanias.add(campania);
        campania.getClientes().add(this); // Agregar el cliente a la campaña
    }
}
