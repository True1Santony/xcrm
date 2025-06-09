package com.xcrm.repository;

import com.xcrm.model.ContactoMensaje;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interfaz que gestiona el acceso a la base de datos para los mensajes de contacto.
 * Extiende JpaRepository para ofrecer operaciones básicas como guardar, buscar y borrar mensajes.
 * Aquí no se añaden métodos extra porque con los que proporciona JpaRepository es suficiente.
 */
public interface ContactoMensajeRepository extends JpaRepository<ContactoMensaje, Long> {
}