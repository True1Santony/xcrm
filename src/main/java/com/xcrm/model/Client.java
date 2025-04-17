package com.xcrm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clientes")
public class Client implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Email(message = "Debe ser un email válido")
    private String email;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @ManyToOne
    @JoinColumn(name = "organization_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_cliente_organizacion"))
    private Organization organization;

    @ManyToMany
    @JoinTable(
            name = "clientes_campanias",
            joinColumns = @JoinColumn(name = "cliente_id"),
            inverseJoinColumns = @JoinColumn(name = "campania_id")
    )
    private Set<Campaign> campaigns = new HashSet<>(); // Relación con las campañas

    @ManyToMany(mappedBy = "clients")
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

    public Organization getOrganizacion() {
        return organization;
    }

    public void setOrganizacion(Organization organization) {
        this.organization = organization;
    }

    public Set<Campaign> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(Set<Campaign> campaigns) {
        this.campaigns = campaigns;
    }

    public void addCampaign(Campaign campaign) {
        campaigns.add(campaign);
        campaign.getClientes().add(this); // Agregar el cliente a la campaña
    }
}
