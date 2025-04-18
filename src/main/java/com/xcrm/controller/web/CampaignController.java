package com.xcrm.controller.web;

import com.xcrm.model.Campaign;
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
        model.addAttribute("campaigns", campaignService.findAll());
        model.addAttribute("new_campaign", new Campaign());

        return "campaigns-administration-dashboard";
    }

    @PostMapping()
    public String createCampaign(
            @ModelAttribute("new_campaign") Campaign campaign,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validaciones, si hay errores, carga de nuevo la plantilla
        if (result.hasErrors()) {
            model.addAttribute("organization", organizationService.getCurrentOrganization());
            model.addAttribute("campaigns", campaignService.findAll());
            return "campaigns-administration-dashboard";
        }

        try {
            campaign.setOrganizacion(organizationService.getCurrentOrganization());
            campaignService.save(campaign);
            redirectAttributes.addFlashAttribute("mensaje", "Campaña creada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear campaña: " + e.getMessage());
            return "redirect:/campaigns/administration";
        }

        return "redirect:/campaigns/administration";
    }

    @PostMapping("/delete")
    public String deleteCampaign(@RequestParam Long id, RedirectAttributes redirectAttributes){
        campaignService.deleteById(id);
        redirectAttributes.addFlashAttribute("mensaje", "Campaña eliminada exitosamente");
        return "redirect:/campaigns/administration";
    }

}
