package com.xcrm.service;

import com.xcrm.model.Campaign;
import com.xcrm.model.Client;
import com.xcrm.repository.CampaignRepository;
import com.xcrm.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ClientRepository clientRepository;

    // Asignar una campaña a un cliente o comercial (mediante las tablas intermedias)
    @Transactional
    public void addClienteToCampania(Long clienteId, Long campaniaId) {
        Optional<Campaign> campaniaOpt = campaignRepository.findById(campaniaId);
        Optional<Client> clienteOpt = clientRepository.findById(clienteId);

        if (campaniaOpt.isPresent() && clienteOpt.isPresent()) {
            Campaign campaign = campaniaOpt.get();
            Client client = clienteOpt.get();
            // La relación entre cliente y campaña puede gestionarse a través de la tabla intermedia
            client.getCampaigns().add(campaign);
            clientRepository.save(client); // Guardamos al cliente con la relación actualizada
        }
    }

    public List<Campaign> findAll() {
       return campaignRepository.findAll();
    }

    public void save(Campaign campaign) {
        campaignRepository.save(campaign);
    }

    public Optional<Campaign> findById(Long id){
        return campaignRepository.findById(id);
    }

    public void deleteById(Long id) {
        campaignRepository.deleteById(id);
    }


    // Implementación del método update
    public Campaign update(Campaign campaign) {
        // Asegurarse de que la campaña tenga un id y exista en la base de datos
        if (campaign != null && campaign.getId() != null) {
            Optional<Campaign> existingCampaign = campaignRepository.findById(campaign.getId());
            if (existingCampaign.isPresent()) {
                // Actualizar los detalles de la campaña existente
                Campaign updatedCampaign = existingCampaign.get();
                updatedCampaign.setNombre(campaign.getNombre());
                updatedCampaign.setDescripcion(campaign.getDescripcion());
                updatedCampaign.setFechaInicio(campaign.getFechaInicio());
                updatedCampaign.setFechaFin(campaign.getFechaFin());
                updatedCampaign.setOrganizacion(campaign.getOrganizacion());
                updatedCampaign.setClientes(campaign.getClientes());
                // Guardar la campaña actualizada
                return campaignRepository.save(updatedCampaign);
            } else {
                throw new IllegalArgumentException("La campaña con id " + campaign.getId() + " no existe");
            }
        }
        throw new IllegalArgumentException("Campaña inválida o sin id");
    }

    public List<Campaign> findAllByIds(List<Long> campaignIds) {
       return campaignRepository.findAllById(campaignIds);
    }
}
