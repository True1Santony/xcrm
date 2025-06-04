package com.xcrm.service;

import com.xcrm.model.Campaign;
import com.xcrm.model.Client;
import com.xcrm.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private ClientService clientService;

    // Constructor para inyectar el repositorio de campañas
    @Autowired
    public CampaignServiceImpl(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    // Setter para inyectar el servicio de clientes (con Lazy para evitar dependencias circulares)
    @Autowired
    public void setClientService(@Lazy ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Añade un cliente a una campaña, actualizando la relación en la base de datos.
     * Comprueba que ambos existan y luego guarda la asociación.
     */
    @Transactional
    public void addClientToCampaign(Long clientId, Long campaignId) {
        Optional<Campaign> campaignOpt = campaignRepository.findById(campaignId);
        Optional<Client> clientOpt = clientService.findById(clientId);

        if (campaignOpt.isPresent() && clientOpt.isPresent()) {
            Client client = clientOpt.get();
            Campaign campaign = campaignOpt.get();
            client.getCampaigns().add(campaign);
            clientService.save(client); // Guarda el cliente con la campaña asociada
        }
    }

    // Devuelve todas las campañas almacenadas en la base de datos.
    public List<Campaign> findAll() {
        return campaignRepository.findAll();
    }

    // Guarda una nueva campaña o actualiza una existente.
    public void save(Campaign campaign) {
        campaignRepository.save(campaign);
    }

    // Busca una campaña por su identificador y devuelve el resultado.
    public Optional<Campaign> findById(Long id){
        return campaignRepository.findById(id);
    }

     // Elimina una campaña identificada por su ID.
    public void deleteById(Long id) {
        campaignRepository.deleteById(id);
    }

    /**
     * Actualiza los datos de una campaña existente.
     * Valida que la campaña sea válida y que exista antes de actualizar.
     * Si no existe o es inválida, lanza una excepción.
     */
    public Campaign update(Campaign campaign) {
        if (campaign == null || campaign.getId() == null) {
            throw new IllegalArgumentException("Campaña inválida o sin id");
        }

        Optional<Campaign> existingCampaignOpt = campaignRepository.findById(campaign.getId());
        if (existingCampaignOpt.isEmpty()) {
            throw new IllegalArgumentException("La campaña con id " + campaign.getId() + " no existe");
        }

        Campaign existingCampaign = existingCampaignOpt.get();
        existingCampaign.setNombre(campaign.getNombre());
        existingCampaign.setDescripcion(campaign.getDescripcion());
        existingCampaign.setFechaInicio(campaign.getFechaInicio());
        existingCampaign.setFechaFin(campaign.getFechaFin());
        existingCampaign.setOrganizacion(campaign.getOrganizacion());
        existingCampaign.setClientes(campaign.getClientes());

        return campaignRepository.save(existingCampaign);
    }

    // Busca y devuelve una lista de campañas a partir de una lista de IDs.
    public List<Campaign> findAllByIds(List<Long> campaignIds) {
        return campaignRepository.findAllById(campaignIds);
    }
}