package com.xcrm.service;

import com.xcrm.model.Campaign;
import com.xcrm.model.Client;
import com.xcrm.model.User;
import com.xcrm.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Lazy;

import java.util.*;

@AllArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    // Repositorios y servicios necesarios para operaciones con clientes, campañas, usuarios y organización
    private final ClientRepository clientRepository;
    private final OrganizationService organizationServiceImpl;
    @Lazy
    private final CampaignService campaignService;
    private final UserService userServiceImpl;

    // Busca y devuelve un cliente por su ID.
    @Override
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    // Devuelve una lista con todos los clientes registrados.
    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    // Devuelve una lista de clientes que coinciden con los IDs proporcionados.
    @Override
    public List<Client> findAllByIds(List<Long> ids) {
        return clientRepository.findAllById(ids);
    }

    /**
     * Crea un nuevo cliente, asignándolo a la organización actual,
     * vinculándolo a campañas y comerciales indicados, y lo guarda.
     */
    @Override
    public Client createClient(Client client, List<Long> campaignIds, List<UUID> comercialIds) {
        client.setOrganizacion(organizationServiceImpl.getCurrentOrganization());
        client.setCampaigns(buildCampaignSet(campaignIds));
        client.setComerciales(buildComercialesSet(comercialIds, client));
        return clientRepository.save(client);
    }

    // Elimina un cliente dado su ID.
    @Override
    public void deleteById(Long id) {
        clientRepository.deleteById(id);
    }

    /**
     * Actualiza los datos básicos de un cliente existente,
     * reasigna campañas y representantes de ventas,
     * y persiste los cambios en la base de datos.
     * Usa transacción para asegurar consistencia.
     */
    @Override
    @Transactional
    public void updateClient(Client incomingClient, List<Long> campaignIds, UUID[] salesRepresentativesIds) {
        Client client = clientRepository.findById(incomingClient.getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Actualizar datos simples del cliente
        client.setNombre(incomingClient.getNombre());
        client.setEmail(incomingClient.getEmail());
        client.setTelefono(incomingClient.getTelefono());
        client.setDireccion(incomingClient.getDireccion());

        // Actualizar campañas asociadas
        client.setCampaigns(buildCampaignSet(campaignIds));

        // Actualizar comerciales relacionados (ventas)
        if (salesRepresentativesIds != null && salesRepresentativesIds.length > 0) {
            // Eliminar asociaciones previas de comerciales con este cliente
            for (User oldComercial : client.getComerciales()) {
                oldComercial.getClientes().remove(client);
            }
            // Asociar nuevos comerciales
            Set<User> newComerciales = new HashSet<>();
            for (UUID id : salesRepresentativesIds) {
                User comercial = userServiceImpl.findById(id);
                if (comercial != null) {
                    comercial.getClientes().add(client);
                    newComerciales.add(comercial);
                }
            }
            client.setComerciales(newComerciales);
        } else {
            // Si no hay comerciales nuevos, limpiar asociaciones previas
            for (User oldComercial : client.getComerciales()) {
                oldComercial.getClientes().remove(client);
            }
            client.setComerciales(new HashSet<>());
        }

        clientRepository.save(client);
    }

    // Guarda un cliente (puede ser nuevo o actualizado).
    @Override
    public void save(Client client) {
        clientRepository.save(client);
    }

    /*
     * Métodos auxiliares privados para construir conjuntos de campañas y comerciales
     * a partir de sus IDs.
     */

    private Set<Campaign> buildCampaignSet(List<Long> campaignIds) {
        if (campaignIds == null || campaignIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<Campaign> campaignSet = new HashSet<>();
        for (Long id : campaignIds) {
            campaignSet.add(campaignService.findById(id).orElse(null));
        }
        return campaignSet;
    }

    private Set<User> buildComercialesSet(List<UUID> comercialIds, Client client) {
        if (comercialIds == null || comercialIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<User> userSet = new HashSet<>();
        for (UUID id : comercialIds) {
            User comercial = userServiceImpl.findById(id);
            if (comercial != null) {
                userSet.add(comercial);
                comercial.getClientes().add(client); // Añadir cliente al comercial para mantener relación bidireccional
            }
        }
        return userSet;
    }
}