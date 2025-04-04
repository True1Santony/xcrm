package com.xcrm.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "campanias")
public class Campania implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacion_id", nullable = false)
    private Organizacion organizacion;// Relación con la organización

    @ManyToMany(mappedBy = "campanias")
    private Set<Cliente> clientes = new HashSet<>(); // Relación con los clientes

    @ManyToMany
    @JoinTable(
            name = "comerciales_campanias",
            joinColumns = @JoinColumn(name = "campania_id"),
            inverseJoinColumns = @JoinColumn(name = "comercial_id")
    )
    private Set<User> comerciales = new HashSet<>();// Relación con los comerciales

    public Campania() {
    }

    public Campania(String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin, Organizacion organizacion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.organizacion = organizacion;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Organizacion getOrganizacion() {
        return organizacion;
    }

    public void setOrganizacion(Organizacion organizacion) {
        this.organizacion = organizacion;
    }

    public Set<User> getComerciales() {
        return comerciales;
    }

    public void setComerciales(Set<User> comerciales) {
        this.comerciales = comerciales;
    }

    public void addComercial(User comercial) {
        comerciales.add(comercial);
        comercial.getCampanias().add(this); // También agregamos la campaña al comercial
    }

    public Set<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(Set<Cliente> clientes) {
        this.clientes = clientes;
    }
}
