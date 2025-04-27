package com.xcrm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contacto_mensaje")
public class ContactoMensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // ID único para cada mensaje

    private String nombre;  // Nombre del remitente
    private String email;   // Email del remitente
    private String asunto;  // Asunto del mensaje

    @Column(length = 5000)
    private String mensaje;  // Contenido del mensaje

    private String archivoDropboxUrl;  // URL del archivo adjunto en Dropbox

    private String archivoNombre;  // Nombre del archivo adjunto, si existe

    @Column(name = "usuarioId")
    private String usuarioId;  // UUID asociado al usuario, almacenado como texto

    private LocalDateTime fechaEnvio;  // Fecha y hora del envío del mensaje

    // Getters y Setters
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

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getArchivoDropboxUrl() {
        return archivoDropboxUrl;
    }

    public void setArchivoDropboxUrl(String archivoDropboxUrl) {
        this.archivoDropboxUrl = archivoDropboxUrl;
    }

    public String getArchivoNombre() {
        return archivoNombre;
    }

    public void setArchivoNombre(String archivoNombre) {
        this.archivoNombre = archivoNombre;
    }

    public String getUsuarioId() { // Relación con el UUID de Usuario
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) { // Relación con el UUID de Usuario
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
}