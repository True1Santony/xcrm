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

        if (mensaje.getUsuarioId() == null) {
            // Solo genera UUID si no se ha asignado previamente
            UUID usuarioId = UUID.randomUUID();
            mensaje.setUsuarioId(usuarioId.toString());
        }
        // Guarda el mensaje en la base de datos
        return contactoMensajeRepository.save(mensaje);  // Persistimos el mensaje en la base de datos
    }
}