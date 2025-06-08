package com.xcrm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "campanias")
public class Campaign implements Serializable {

    // Representa una campaña comercial dentro del sistema CRM.
    // Contiene información básica, fechas de duración, organización relacionada
    // y relaciones con clientes y usuarios comerciales.

    // Identificador único de la campaña
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre de la campaña (hasta 100 caracteres)
    @Column(nullable = false, length = 100)
    private String nombre;

    // Descripción detallada de la campaña
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Fecha de inicio de la campaña (obligatoria)
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    // Fecha de fin de la campaña (obligatoria)
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    // Organización a la que pertenece la campaña (relación muchos-a-uno)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacion_id", nullable = false)
    private Organization organizacion;

    // Lista de clientes asignados a esta campaña (relación muchos-a-muchos)
    @ManyToMany
    @JoinTable(
            name = "clientes_campanias",
            joinColumns = @JoinColumn(name = "campania_id"),
            inverseJoinColumns = @JoinColumn(name = "cliente_id")
    )
    private Set<Client> clientes = new HashSet<>();

    // Lista de usuarios comerciales asignados a esta campaña (relación muchos-a-muchos)
    @ManyToMany
    @JoinTable(
            name = "comerciales_campanias",
            joinColumns = @JoinColumn(name = "campania_id"),
            inverseJoinColumns = @JoinColumn(name = "comercial_id")
    )
    private Set<User> comerciales = new HashSet<>();

    // Constructor vacío requerido por JPA
    public Campaign() {
    }

    // Constructor con todos los campos necesarios para crear una campaña
    public Campaign(String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin, Organization organizacion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.organizacion = organizacion;
    }
}