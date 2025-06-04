package com.xcrm.service;

import com.xcrm.model.Campaign;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define los métodos clave para gestionar campañas.
 * Incluye operaciones para crear, consultar, actualizar, eliminar
 * y asociar clientes a campañas.
 */
public interface CampaignService {

    // Guarda una nueva campaña en la base de datos
    void save(Campaign campaign);

    // Actualiza los datos de una campaña existente
    Campaign update(Campaign campaign);

    // Devuelve la lista completa de campañas
    List<Campaign> findAll();

    // Busca una campaña por su ID
    Optional<Campaign> findById(Long id);

    // Busca varias campañas a partir de una lista de IDs
    List<Campaign> findAllByIds(List<Long> campaignIds);

    // Elimina una campaña por su ID
    void deleteById(Long id);

    // Asocia un cliente específico a una campaña
    void addClientToCampaign(Long clientId, Long campaignId);
}