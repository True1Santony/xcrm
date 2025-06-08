/*

public interface ClientService {
    Optional<Client> findById(Long id);
    List<Client> findAll();
    List<Client> findAllByIds(List<Long> ids);
    Client createClient(Client client, List<Long> campaignIds, List<UUID> comercialIds);
    void deleteById(Long id);
    void updateClient(Client incomingClient, List<Long> campaignIds, UUID[] salesRepresentativesIds);
    void save(Client client);
}
*/
package com.xcrm.service;

import com.xcrm.model.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientService {

    /**
     * Busca un cliente por su ID.
     * Método para obtener un cliente específico dado su identificador único.
     */
    Optional<Client> findById(Long id);

    /**
     * Obtiene la lista de todos los clientes.
     * Recupera todos los clientes almacenados sin filtros.
     */
    List<Client> findAll();

    /**
     * Obtiene una lista de clientes a partir de una lista de IDs.
     * Permite buscar múltiples clientes usando una colección de identificadores.
     */
    List<Client> findAllByIds(List<Long> ids);

    /**
     * Crea un nuevo cliente asociado a campañas y comerciales.
     * Añade un cliente nuevo y lo vincula con campañas y comerciales específicos.
     */
    Client createClient(Client client, List<Long> campaignIds, List<UUID> comercialIds);

    /**
     * Elimina un cliente por su ID.
     * Borra un cliente existente identificado por su ID.
     */
    void deleteById(Long id);

    /**
     * Actualiza un cliente y sus asociaciones con campañas y representantes de ventas.
     * Recibe un arreglo de IDs de representantes de ventas para actualizar las relaciones.
     */
    void updateClient(Client incomingClient, List<Long> campaignIds, UUID[] salesRepresentativesIds);

    /**
     * Guarda un cliente (crea o actualiza).
     * Método genérico para persistir un cliente, sea nuevo o modificado.
     */
    void save(Client client);
}