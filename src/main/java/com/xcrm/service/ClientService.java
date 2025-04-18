package com.xcrm.service;

import com.xcrm.model.Campaign;
import com.xcrm.model.Client;
import com.xcrm.model.User;
import com.xcrm.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private UserService userService;

    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client createClient(Client client, List<Long> campaignIds, List<UUID> comercialIds) {

        client.setOrganizacion(organizationService.getCurrentOrganization());

        if (campaignIds != null && !campaignIds.isEmpty()){
            Set<Campaign> campaignSet = new HashSet<>();
            for (Long id: campaignIds){
                campaignSet.add(campaignService.findById(id).orElse(null));
            }
            client.setCampaigns(campaignSet);
        }

        if (comercialIds != null && !comercialIds.isEmpty()) {
            Set<User> userSet = new HashSet<>();
            for (UUID id: comercialIds){
                User comercial = userService.findById(id);
                if (comercial != null) {
                    userSet.add(comercial);

                    comercial.getClientes().add(client);//agregar al user el cliente (Ya que es el propietario)
                }
            }
            client.setComerciales(userSet);
        }

        return clientRepository.save(client);
    }

    public void deleteById(Long id) {
        clientRepository.deleteById(id);
    }

    @Transactional
    public void updateClient(Client client, List<Long> campaignIds, UUID[] salesRepresentativesIds) {

        if (campaignIds != null &&  !campaignIds.isEmpty()) {
            Set<Campaign> campaigns = new HashSet<>();
            for (Long id : campaignIds) {
                campaigns.add(campaignService.findById(id).orElse(null));
            }
            client.setCampaigns(campaigns);
        } else {
            client.setCampaigns(new HashSet<>());
        }

        if (salesRepresentativesIds != null && salesRepresentativesIds.length > 0) {
            // Limpiar relaciones antiguas
            for (User oldComercial : client.getComerciales()) {
                oldComercial.getClientes().remove(client);
            }

            Set<User> salesRepresentatives = new HashSet<>();
            for (UUID id : salesRepresentativesIds) {
                User comercial = userService.findById(id);
                if (comercial != null) {
                    // Verificar si el cliente ya está asignado a este comercial
                    if (!comercial.getClientes().contains(client)) {
                        comercial.getClientes().add(client); // Agregar solo si no existe
                    }
                    salesRepresentatives.add(comercial);
                }
            }
            client.setComerciales(salesRepresentatives);
        } else {
            // Si no hay comerciales seleccionados, limpiar todas las relaciones
            for (User oldComercial : client.getComerciales()) {
                oldComercial.getClientes().remove(client);
            }
            client.setComerciales(new HashSet<>());
        }

        clientRepository.save(client);
    }
}
