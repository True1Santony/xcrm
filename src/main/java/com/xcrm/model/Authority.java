package com.xcrm.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "authorities")
@IdClass(AuthorityId.class)
public class Authority implements Serializable {

    @Id
    @Column(name = "username", nullable = false)  // Añadir el campo username aquí
    private String username;  // Campo username que corresponde a User

    @Id
    @Column(name = "authority", nullable = false)
    private String authority;

    @ManyToOne // Relación ManyToOne con User
    @JoinColumn(name = "username", insertable = false, updatable = false)
    private User user;  // Aquí va la propiedad 'user'

    // Constructor vacío
    public Authority() {
    }

    // Constructor
    public Authority(String username, String authority) {
        this.username = username;
        this.authority = authority;
    }

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
