package com.xcrm.service;

import com.xcrm.model.ContactoMensaje;
import com.xcrm.repository.ContactoMensajeRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servicio que maneja la lógica para recibir y guardar mensajes del formulario de contacto.
 * Se asegura de asignar fecha de envío y generar un identificador de usuario si no existe,
 * antes de guardar el mensaje en la base de datos.
 */
@Service
public class ContactoMensajeService {

    private final ContactoMensajeRepository contactoMensajeRepository;

    // Uso inyección por constructor para que el código sea más claro y fácil de testear
    public ContactoMensajeService(ContactoMensajeRepository contactoMensajeRepository) {
        this.contactoMensajeRepository = contactoMensajeRepository;
    }

    /**
     * Guarda el mensaje recibido, asignando fecha actual y un ID de usuario si es necesario.
     * Devuelve el mensaje ya guardado con su ID y fecha asignados.
     */
    public ContactoMensaje guardarMensajeContacto(ContactoMensaje mensaje) {
        // Pone la fecha y hora actuales en el mensaje
        mensaje.setFechaEnvio(LocalDateTime.now());

        // Si el mensaje no tiene usuario asociado, crea uno anónimo nuevo
        if (mensaje.getUsuarioId() == null) {
            mensaje.setUsuarioId(UUID.randomUUID());
        }

        // Guarda el mensaje en la base de datos y devuelve la entidad persistida
        return contactoMensajeRepository.save(mensaje);
    }
}