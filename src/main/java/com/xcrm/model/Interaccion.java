package com.xcrm.model;
import jakarta.persistence.*;

import java.io.Serializable;

import java.time.LocalDateTime;

@Entity
@Table(name = "interacciones")
public class Interaccion implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comercial_id", nullable = false)
    private User comercial; // Relación con el comercial (usuario)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Client client; // Relación con el cliente

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campania_id")
    private Campaign campaign; // Relación con la campaña (puede ser NULL)

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, length = 50)
    private String tipo; // Tipo de interacción (llamada, reunión, etc.)

    @Column(nullable = false, length = 50)
    private String estado; // Estado de la interacción (pendiente, realizada, etc.)

    @Column(columnDefinition = "TEXT")
    private String notas; // Notas adicionales sobre la interacción

    @OneToOne(mappedBy = "interaccion")
    private Venta venta; // Relación con la venta asociada

    public Interaccion() {
    }

    public Interaccion(User comercial, Client client, Campaign campaign, LocalDateTime fechaHora, String tipo, String estado, String notas) {
        this.comercial = comercial;
        this.client = client;
        this.campaign = campaign;
        this.fechaHora = fechaHora;
        this.tipo = tipo;
        this.estado = estado;
        this.notas = notas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getComercial() {
        return comercial;
    }

    public void setComercial(User comercial) {
        this.comercial = comercial;
    }

    public Client getCliente() {
        return client;
    }

    public void setCliente(Client client) {
        this.client = client;
    }

    public Campaign getCampania() {
        return campaign;
    }

    public void setCampania(Campaign campaign) {
        this.campaign = campaign;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }
}
