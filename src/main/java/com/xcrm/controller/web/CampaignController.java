package com.xcrm.controller.web;

import com.xcrm.model.Campaign;
import com.xcrm.model.Organization;
import com.xcrm.service.CampaignService;
import com.xcrm.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/campaigns")
public class CampaignController {

    @Autowired
    CampaignService campaignService;

    @Autowired
    OrganizationService organizationService;

    @GetMapping("/administration")
    public String getCampaignsAdministrationDashboard(Model model){
        model.addAttribute("organization", organizationService.getCurrentOrganization());
        model.addAttribute("campaigns", campaignService.findAll()); // Paso las campañas existentes.
        model.addAttribute("new_campaign", new Campaign());

        return "campaigns-administration-dashboard";
    }

    @PostMapping
    public String createCampaign(
            @ModelAttribute("new_campaign") Campaign campaign,
            @RequestParam Long organizacionId, // Recibe el ID de la organización desde el formulario
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validaciones básicas
        if (result.hasErrors()) {
            model.addAttribute("campaigns", campaignService.findAll());
            return "campaigns-administration-dashboard";
        }

        try {
            // 1. Buscar la organización
            Organization organization = organizationService.findById(organizacionId)
                    .orElseThrow(() -> new IllegalArgumentException("Organización no encontrada"));

            // 2. Asignar la organización a la campaña
            campaign.setOrganizacion(organization);

            // 3. Guardar la campaña
            campaignService.save(campaign);

            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("mensaje", "Campaña creada exitosamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear campaña: " + e.getMessage());
            return "redirect:/campaigns/administration";
        }

        return "redirect:/campaigns/administration";
    }

}
