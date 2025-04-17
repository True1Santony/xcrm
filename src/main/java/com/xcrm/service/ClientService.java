package com.xcrm.service;

import com.xcrm.model.Campaign;
import com.xcrm.model.Client;
import com.xcrm.model.User;
import com.xcrm.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
