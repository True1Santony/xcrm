package com.xcrm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @Column(columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    private UUID id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Column(unique = true, nullable = false, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    private boolean enabled;

    @ManyToOne
    @JoinColumn(name = "organizacion_id", nullable = false)
    @JsonBackReference // Indica el lado inverso.api
    private Organization organization;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Authority> authorities = new HashSet<>(); // Relación con Authority

    @ManyToMany
    @JoinTable(
            name = "comerciales_campanias",
            joinColumns = @JoinColumn(name = "comercial_id"),
            inverseJoinColumns = @JoinColumn(name = "campania_id")
    )
    private Set<Campaign> campaigns = new HashSet<>(); // Relación con las campañas, es el propietario

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "comerciales_clientes",
            joinColumns = @JoinColumn(name = "comercial_id"),
            inverseJoinColumns = @JoinColumn(name = "cliente_id")
    )
    private Set<Client> clients = new HashSet<>(); // Relación con los clientes

    private String fotoUrl;

    // Constructor, getters y setters
    public User() {
    }

    public User(UUID id, String username, String password, boolean enabled, Organization organization) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.organization = organization;
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

//    // Email
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }

    public Organization getOrganizacion() {
        return organization;
    }

    public void setOrganizacion(Organization organization) {
        this.organization = organization;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public Set<Campaign> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(Set<Campaign> campaigns) {
        this.campaigns = campaigns;
    }

    public void addCampania(Campaign campaign) {
        campaigns.add(campaign);
        campaign.getComerciales().add(this); // Agregar el comercial a la campaña
    }

    public Set<Client> getClientes() {
        return clients;
    }

    public void setClientes(Set<Client> clients) {
        this.clients = clients;
    }
    public void addCliente(Client client) {
        clients.add(client);
        client.getComerciales().add(this); // Agregar el comercial al cliente
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}