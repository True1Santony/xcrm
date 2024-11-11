package com.xcrm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "authorities")
@IdClass(AuthorityId.class)
public class Authority implements Serializable {

    @Id
    @Column(name = "username", nullable = false)
    private String username;  // Campo username que corresponde a User

    @Id
    @Column(name = "authority", nullable = false)
    private String authority;

    @ManyToOne // Relación ManyToOne con User
    @JoinColumn(name = "username", insertable = false, updatable = false)
    @JsonIgnore // ignora a la hora de serializar, ya se obtienen del lado de organizacion. api
    private User user;

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
