package com.xcrm.controller.web;

import com.xcrm.model.Campania;
import com.xcrm.model.Organizacion;
import com.xcrm.service.CampaniaService;
import com.xcrm.service.OrganizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/campaigns")
public class CampaignController {

    @Autowired
    CampaniaService campaignService;

    @Autowired
    OrganizacionService organizacionService;

    @GetMapping("/administration")
    public String getCampaignsAdministrationDashboard(Model model){
        model.addAttribute("organizacion", organizacionService.getOrganizacionActual());
        model.addAttribute("campaigns", campaignService.getAll()); // Paso las campañas existentes.
        model.addAttribute("new_campaign", new Campania());

        return "campaigns-administration-dashboard";
    }

    @PostMapping
    public String createCampaign(
            @ModelAttribute("new_campaign") Campania campania,
            @RequestParam Long organizacionId, // Recibe el ID de la organización desde el formulario
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validaciones básicas
        if (result.hasErrors()) {
            model.addAttribute("campaigns", campaignService.getAll());
            return "campaigns-administration-dashboard";
        }

        try {
            // 1. Buscar la organización
            Organizacion organizacion = organizacionService.findById(organizacionId)
                    .orElseThrow(() -> new IllegalArgumentException("Organización no encontrada"));

            // 2. Asignar la organización a la campaña
            campania.setOrganizacion(organizacion);

            // 3. Guardar la campaña
            campaignService.save(campania);

            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("mensaje", "Campaña creada exitosamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear campaña: " + e.getMessage());
            return "redirect:/campaigns/administration";
        }

        return "redirect:/campaigns/administration";
    }

}
