package com.xcrm.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ventas")
public class Venta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "interaccion_id", nullable = false)
    private Interaccion interaccion; // Relación con la interacción asociada

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto; // Monto de la venta

    @Column(name = "producto", nullable = false, length = 255)
    private String producto; // Producto o servicio vendido

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    public Venta() {
    }

    public Venta(Interaccion interaccion, BigDecimal monto, String producto, LocalDate fecha) {
        this.interaccion = interaccion;
        this.monto = monto;
        this.producto = producto;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Interaccion getInteraccion() {
        return interaccion;
    }

    public void setInteraccion(Interaccion interaccion) {
        this.interaccion = interaccion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
