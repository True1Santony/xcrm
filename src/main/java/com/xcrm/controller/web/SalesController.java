package com.xcrm.controller.web;

import com.xcrm.model.Campaign;
import com.xcrm.model.Client;
import com.xcrm.model.Interaccion;
import com.xcrm.model.User;
import com.xcrm.service.CampaignService;
import com.xcrm.service.OrganizationService;
import com.xcrm.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/sales")
public class SalesController {

    private final CampaignService campaignService;
    private final UserService userService;
    private final OrganizationService organizationService;

    public SalesController(CampaignService campaignService, UserService userService, OrganizationService organizationService){
        this.campaignService = campaignService;
        this.userService = userService;
        this.organizationService = organizationService;
    }


    @GetMapping
    public String getSalesAdministrationDashboard(Authentication authentication, Model model){
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        model.addAttribute("allComerciales", userService.findAll());
        model.addAttribute("allCampaigns", campaignService.findAll());
        model.addAttribute("organization", organizationService.getCurrentOrganization());
        model.addAttribute("campaignsForUser", (user.getCampaigns()));
        return "sales-administration-dashboard";
    }

    @PostMapping("/assign")
    public String assignCampaignsToUser(@RequestParam("userId") UUID userId,
                                        @RequestParam("campaignIds") List<Long> campaignIds,
                                        RedirectAttributes redirectAttributes) {
        User user = userService.findById(userId);
        Set<Campaign> selectedCampaigns = new HashSet<>(campaignService.findAllByIds(campaignIds));
        user.setCampaigns(selectedCampaigns);
        userService.save(user);
        redirectAttributes.addFlashAttribute("mensaje", "Campañas asignadas correctamente a " + user.getUsername());
        return "redirect:/sales";
    }

    @GetMapping("/clientByIdCampaign")
    public String getClientsByCampaign(@RequestParam(name = "campaignId") Long campaignId,
                                       Authentication authentication, Model model){
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        Campaign selectedCampaign = campaignService.findById(campaignId).orElse(null);

        Set<Client> clients = selectedCampaign.getClientes();



        clients.forEach(client -> {
            client.setLastInteraction(
                    client.getInteracciones().stream()
                            .max(Comparator.comparing(Interaccion::getFechaHora)) // Ordenamos por fechaHora
                            .orElse(null) // Si no hay interacciones, asignamos null
            );
        });


        model.addAttribute("allComerciales", userService.findAll());
        model.addAttribute("allCampaigns", campaignService.findAll());
        model.addAttribute("organization", organizationService.getCurrentOrganization());
        model.addAttribute("campaignsForUser", (user.getCampaigns()));
        model.addAttribute("selectedCampaign", selectedCampaign);
        model.addAttribute("clients", clients);
        return "sales-administration-dashboard";
    }



}
