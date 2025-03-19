package com.xcrm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "authorities")
public class Authority implements Serializable {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "authority", nullable = false)
    private String authority;

    @ManyToOne // Relación ManyToOne con User
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // ignora a la hora de serializar, ya se obtienen del lado de organizacion. api
    private User user;

    // Constructor vacío
    public Authority() {
    }

    // Constructor
    public Authority(UUID id, User user, String authority) {
        this.id = id;
        this.user = user;
        this.authority = authority;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Authority{" +
                "id=" + id +
                ", authority='" + authority + '\'' +
                ", user=" + user +
                '}';
    }
}
