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
public class ClientServiceImpl implements ClientService{

    private final ClientRepository clientRepository;
    private final OrganizationService organizationServiceImpl;
    @Lazy
    private final CampaignService campaignService;
    private final UserService userServiceImpl;

    @Override
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public List<Client> findAllByIds(List<Long> ids) {
        return clientRepository.findAllById(ids);
    }

    @Override
    public Client createClient(Client client, List<Long> campaignIds, List<UUID> comercialIds) {

        client.setOrganizacion(organizationServiceImpl.getCurrentOrganization());

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
                User comercial = userServiceImpl.findById(id);
                if (comercial != null) {
                    userSet.add(comercial);

                    comercial.getClientes().add(client);//agregar al user el cliente (Ya que es el propietario)
                }
            }
            client.setComerciales(userSet);
        }

        return clientRepository.save(client);
    }

    @Override
    public void deleteById(Long id) {
        clientRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateClient(Client incomingClient, List<Long> campaignIds, UUID[] salesRepresentativesIds) {

        Client client = clientRepository.findById(incomingClient.getId())
                .orElseThrow(()-> new RuntimeException("Cliente no encontrado"));

        // 2. Actualizar los campos simples (nombre, dirección, etc.)
        client.setNombre(incomingClient.getNombre());
        client.setEmail(incomingClient.getEmail());
        client.setTelefono(incomingClient.getTelefono());
        client.setDireccion(incomingClient.getDireccion());

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
            for (User oldComercial : client.getComerciales()) {
                oldComercial.getClientes().remove(client);
            }

            Set<User> newSalesReps = new HashSet<>();
            for (UUID id : salesRepresentativesIds) {
                User comercial = userServiceImpl.findById(id);
                if (comercial != null) {
                    comercial.getClientes().add(client);
                    newSalesReps.add(comercial);
                }
            }

            client.setComerciales(newSalesReps);
        } else {
            for (User oldComercial : client.getComerciales()) {
                oldComercial.getClientes().remove(client);
            }
            client.setComerciales(new HashSet<>());
        }

        clientRepository.save(client);
    }

    @Override
    public void save(Client client) {
        clientRepository.save(client);
    }
}