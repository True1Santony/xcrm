package com.xcrm.service;

import com.xcrm.model.Campaign;
import com.xcrm.model.Client;
import com.xcrm.model.Organization;
import com.xcrm.repository.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CampaignServiceImplTest {
    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private CampaignServiceImpl campaignService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddClienteToCampania_success() {
        Long clientId = 1L;
        Long campaignId = 2L;

        Campaign campaign = new Campaign();
        campaign.setId(campaignId);

        Client client = new Client();
        client.setId(clientId);
        client.setCampaigns(new HashSet<>());

        when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));
        when(clientService.findById(clientId)).thenReturn(Optional.of(client));

        campaignService.addClienteToCampania(clientId, campaignId);

        assertTrue(client.getCampaigns().contains(campaign));
        verify(clientService, times(1)).save(client);
    }

    @Test
    void testFindAll_returnsList() {
        List<Campaign> campaigns = Arrays.asList(new Campaign(), new Campaign());
        when(campaignRepository.findAll()).thenReturn(campaigns);

        List<Campaign> result = campaignService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void testSave_callsRepository() {
        Campaign campaign = new Campaign();
        campaignService.save(campaign);
        verify(campaignRepository).save(campaign);
    }

    @Test
    void testFindById_found() {
        Campaign campaign = new Campaign();
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
        Optional<Campaign> result = campaignService.findById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    void testDeleteById_callsRepository() {
        campaignService.deleteById(1L);
        verify(campaignRepository).deleteById(1L);
    }

    @Test
    void testUpdate_successful() {
        Campaign existing = new Campaign("Old", "desc", LocalDate.now(), LocalDate.now().plusDays(1), new Organization());
        existing.setId(1L);
        Campaign updated = new Campaign("New", "updated", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), new Organization());
        updated.setId(1L);

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(campaignRepository.save(any(Campaign.class))).thenReturn(existing);

        Campaign result = campaignService.update(updated);

        assertEquals("New", result.getNombre());
    }

    @Test
    void testUpdate_invalidCampaign_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> campaignService.update(null));
        assertThrows(IllegalArgumentException.class, () -> campaignService.update(new Campaign()));
    }

    @Test
    void testUpdate_nonexistentCampaign_throwsException() {
        Campaign campaign = new Campaign();
        campaign.setId(99L);
        when(campaignRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> campaignService.update(campaign));
    }

    @Test
    void testFindAllByIds_returnsList() {
        List<Long> ids = Arrays.asList(1L, 2L);
        List<Campaign> campaigns = Arrays.asList(new Campaign(), new Campaign());
        when(campaignRepository.findAllById(ids)).thenReturn(campaigns);

        List<Campaign> result = campaignService.findAllByIds(ids);

        assertEquals(2, result.size());
    }
}
