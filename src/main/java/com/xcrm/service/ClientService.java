package com.xcrm.service;

import com.xcrm.model.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientService {
    Optional<Client> findById(Long id);
    List<Client> findAll();
    List<Client> findAllByIds(List<Long> ids);
    Client createClient(Client client, List<Long> campaignIds, List<UUID> comercialIds);
    void deleteById(Long id);
    void updateClient(Client incomingClient, List<Long> campaignIds, UUID[] salesRepresentativesIds);
    void save(Client client);
}
