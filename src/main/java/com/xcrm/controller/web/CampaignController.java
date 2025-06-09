package com.xcrm.controller.web;

import com.xcrm.model.Campaign;
import com.xcrm.service.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Controller
@RequestMapping("/campaigns")
public class CampaignController {

    private final CampaignService campaignService;
    private final OrganizationService organizationService;
    private final ClientService clientService;

    @GetMapping("/administration")
    public String getCampaignsAdministrationDashboard(Model model){
        model.addAttribute("organization", organizationService.getCurrentOrganization());
        model.addAttribute("campaigns", campaignService.findAll());
        model.addAttribute("new_campaign", new Campaign());
        model.addAttribute("allClients", clientService.findAll());

        return "campaigns-administration-dashboard";
    }

    @PostMapping()
    public String createCampaign(
            @ModelAttribute("new_campaign") Campaign campaign,
            @RequestParam(value = "clientes", required = false) List<Long> clientIds,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("organization", organizationService.getCurrentOrganization());
            model.addAttribute("campaigns", campaignService.findAll());
            return "campaigns-administration-dashboard";
        }

        try {

            campaign.setOrganizacion(organizationService.getCurrentOrganization());

            // Validación de fechas
            if (campaign.getFechaFin().isBefore(campaign.getFechaInicio())) {
                redirectAttributes.addFlashAttribute("error", "La fecha de fin no puede ser anterior a la fecha de inicio.");
                return "redirect:/campaigns/administration";
            }

            // Asociar clientes seleccionados si los hay
            if (clientIds != null && !clientIds.isEmpty()) {
                campaign.setClientes(new HashSet<>(clientService.findAllByIds(clientIds)));
            }

            campaignService.save(campaign);
            redirectAttributes.addFlashAttribute("mensaje", "Campaña creada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear campaña: " + e.getMessage());
            return "redirect:/campaigns/administration";
        }

        return "redirect:/campaigns/administration";
    }

//    @GetMapping("/edit/{id}")
//
//    public String showEditForm(@PathVariable("id") Long id, Model model) {
//        Optional<Campaign> optionalCampaign = campaignService.findById(id);
//
//        if (optionalCampaign.isPresent()) {
//            Campaign campaign = optionalCampaign.get();
//            model.addAttribute("campaign", campaign);
//            return "editCampaign";
//        } else {
//            model.addAttribute("error", "Campaña no encontrada");
//            return "error";
//        }
//
//        Campaign campaign = optionalCampaign.get();
//
//        if (campaign.getFechaInicio().isBefore(LocalDate.now())) {
//            redirectAttributes.addFlashAttribute("error", "No se puede editar una campaña que ya ha comenzado.");
//            return "redirect:/campaigns/administration";
//        }
//
//        model.addAttribute("campaign", campaign);
//        model.addAttribute("allClients", clientService.findAll());
//
//        return "campaign-edit"; // Esta es la vista que contiene el formulario completo
//    }
@GetMapping("/edit/{id}")
public String showEditForm(
        @PathVariable("id") Long id,
        Model model,
        RedirectAttributes redirectAttributes) { // <-- Agregar parámetro

    Optional<Campaign> optionalCampaign = campaignService.findById(id);

    if (optionalCampaign.isEmpty()) {
        redirectAttributes.addFlashAttribute("error", "Campaña no encontrada");
        return "redirect:/campaigns/administration";
    }

    Campaign campaign = optionalCampaign.get();

    if (campaign.getFechaInicio().isBefore(LocalDate.now())) {
        redirectAttributes.addFlashAttribute("error", "No se puede editar una campaña que ya ha comenzado.");
        return "redirect:/campaigns/administration";
    }

    model.addAttribute("campaign", campaign);
    model.addAttribute("allClients", clientService.findAll());

    return "campaign-edit"; // Vista con formulario para editar
}



    @PostMapping("/update")
    public String updateCampaign(
            @RequestParam("id") Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("fechaInicio") String fechaInicio,
            @RequestParam("fechaFin") String fechaFin,
            @RequestParam(value = "clientes", required = false) List<Long> clientIds,
            RedirectAttributes redirectAttributes) {

        Optional<Campaign> optionalCampaign = campaignService.findById(id);

        if (optionalCampaign.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Campaña no encontrada");
            return "redirect:/campaigns/edit/";
        }

        Campaign campaign = optionalCampaign.get();

        if (campaign.getFechaInicio().isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "No se puede editar una campaña que ya ha comenzado.");
            return "redirect:/campaigns/administration";
        }

        try {
            // Validación de fechas
            LocalDate inicio = LocalDate.parse(fechaInicio);
            LocalDate fin = LocalDate.parse(fechaFin);
            if (fin.isBefore(inicio)) {
                redirectAttributes.addFlashAttribute("error", "La fecha de fin no puede ser anterior a la fecha de inicio.");
                return "redirect:/campaigns/edit/" + id;
            }

            campaign.setNombre(nombre);
            campaign.setDescripcion(descripcion);
            campaign.setFechaInicio(LocalDate.parse(fechaInicio));
            campaign.setFechaFin(LocalDate.parse(fechaFin));
            campaign.setOrganizacion(organizationService.getCurrentOrganization());

            // Asociar clientes seleccionados
            if (clientIds != null && !clientIds.isEmpty()) {
                campaign.setClientes(new HashSet<>(clientService.findAllByIds(clientIds)));
            } else {
                campaign.setClientes(new HashSet<>());
            }

            campaignService.update(campaign);
            redirectAttributes.addFlashAttribute("mensaje", "Campaña actualizada exitosamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar campaña: " + e.getMessage());
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