package com.xcrm.service;

import com.xcrm.model.Campaign;

import java.util.List;
import java.util.Optional;

public interface CampaignService {
    void addClienteToCampania(Long clienteId, Long campaniaId);
    List<Campaign> findAll();
    void save(Campaign campaign);
    Optional<Campaign> findById(Long id);
    void deleteById(Long id);
    Campaign update(Campaign campaign);
    List<Campaign> findAllByIds(List<Long> campaignIds);
}
