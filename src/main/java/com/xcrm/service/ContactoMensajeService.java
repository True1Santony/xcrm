package com.xcrm.service;

import com.xcrm.model.ContactoMensaje;
import com.xcrm.repository.ContactoMensajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

// Servicio encargado de recibir y almacenar los mensajes en la base de datos
@Service
public class ContactoMensajeService {

    @Autowired
    private ContactoMensajeRepository contactoMensajeRepository;

    /**
     * Guarda un mensaje en la base de datos.
     * @param mensaje El mensaje a guardar.
     * @return El mensaje guardado.
     */
    public ContactoMensaje guardarMensaje(ContactoMensaje mensaje) {
        // Se establece la fecha y hora del envío para el mensaje
        mensaje.setFechaEnvio(LocalDateTime.now());

        // Si el usuario está logueado, se obtiene su ID desde el ContactoController.java
        // Genera un UUID si el usuario no está logueado (para usuarios anónimos)
        UUID usuarioId = UUID.randomUUID();  // Generamos un UUID para el mensaje si no hay usuario logueado
        mensaje.setUsuarioId(usuarioId.toString());  // Asignamos el UUID generado o el ID del usuario logueado

        // Guarda el mensaje en la base de datos
        return contactoMensajeRepository.save(mensaje);  // Persistimos el mensaje en la base de datos
    }
}