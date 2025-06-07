package com.xcrm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contacto_mensaje")
public class ContactoMensaje {

    // Representa un mensaje enviado a través del formulario de contacto.
    // Cada mensaje tiene un ID único generado automáticamente para identificarlo en la base de datos.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Datos básicos del remitente: nombre, email y asunto del mensaje.

    private String nombre;
    private String email;
    private String asunto;

    // Contenido principal del mensaje, con un tamaño máximo permitido.

    @Column(length = 5000)
    private String mensaje;

    // Información sobre el archivo adjunto, que puede estar almacenado en Dropbox.
    // Se guarda la URL del archivo en Dropbox y su nombre para referencia.

    private String archivoDropboxUrl;
    private String nombreArchivoAdjunto;

    // Identificador del usuario que envió el mensaje, para relacionarlo con la cuenta correspondiente.
    // Se almacena como texto (UUID en formato string).

    @Column(name = "usuarioId")
    private String usuarioId;

    // Fecha y hora en que se envió el mensaje, para registro y orden cronológico.

    private LocalDateTime fechaEnvio;

    // Métodos estándar para obtener y modificar los valores de cada campo.
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

    public String getNombreArchivoAdjunto() {
        return nombreArchivoAdjunto;
    }

    public void setNombreArchivoAdjunto(String nombreArchivoAdjunto) {
        this.nombreArchivoAdjunto = nombreArchivoAdjunto;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
}