package com.xcrm.controller.web;

import com.xcrm.model.Campaign;
import com.xcrm.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/campaigns")
public class CampaignController {

    @Autowired
    CampaignService campaignServiceImpl;

    @Autowired
    OrganizationService organizationServiceImpl;

    @Autowired
    private ClientServiceImpl clientServiceImpl; //Ahora busca clientes por su ID

    @GetMapping("/administration")
    public String getCampaignsAdministrationDashboard(Model model){
        model.addAttribute("organization", organizationServiceImpl.getCurrentOrganization());
        model.addAttribute("campaigns", campaignServiceImpl.findAll());
        model.addAttribute("new_campaign", new Campaign());
        model.addAttribute("allClients", clientServiceImpl.findAll());

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
            model.addAttribute("organization", organizationServiceImpl.getCurrentOrganization());
            model.addAttribute("campaigns", campaignServiceImpl.findAll());
            return "campaigns-administration-dashboard";
        }

        try {
            campaign.setOrganizacion(organizationServiceImpl.getCurrentOrganization());

            // Asociar clientes seleccionados si los hay
            if (clientIds != null && !clientIds.isEmpty()) {
                campaign.setClientes(new HashSet<>(clientServiceImpl.findAllByIds(clientIds)));
            }

            campaignServiceImpl.save(campaign);
            redirectAttributes.addFlashAttribute("mensaje", "Campaña creada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear campaña: " + e.getMessage());
            return "redirect:/campaigns/administration";
        }

        return "redirect:/campaigns/administration";
    }

//    @GetMapping("/edit/{id}")
//    public String showEditForm(@PathVariable("id") Long id, Model model) {
//        Optional<Campaign> optionalCampaign = campaignServiceImpl.findById(id);
//
//        if (optionalCampaign.isPresent()) {
//            Campaign campaign = optionalCampaign.get(); // Obtén la campaña si está presente
//            model.addAttribute("campaign", campaign); // Pasa la campaña al modelo
//            return "editCampaign"; // La vista del formulario de edición
//        } else {
//            // Manejo si la campaña no existe
//            model.addAttribute("error", "Campaña no encontrada");
//            return "error"; // Redirige a una página de error
//        }
//    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Campaign> optionalCampaign = campaignServiceImpl.findById(id);

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
        model.addAttribute("allClients", clientServiceImpl.findAll());

        return "campaign-edit"; // Esta es la vista que contiene el formulario completo
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

        Optional<Campaign> optionalCampaign = campaignServiceImpl.findById(id);

        if (optionalCampaign.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Campaña no encontrada");
            return "redirect:/campaigns/administration";
        }

        Campaign campaign = optionalCampaign.get();

        if (campaign.getFechaInicio().isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "No se puede editar una campaña que ya ha comenzado.");
            return "redirect:/campaigns/administration";
        }

        try {
            campaign.setNombre(nombre);
            campaign.setDescripcion(descripcion);
            campaign.setFechaInicio(LocalDate.parse(fechaInicio));
            campaign.setFechaFin(LocalDate.parse(fechaFin));
            campaign.setOrganizacion(organizationServiceImpl.getCurrentOrganization());

            // Asociar clientes seleccionados
            if (clientIds != null && !clientIds.isEmpty()) {
                campaign.setClientes(new HashSet<>(clientServiceImpl.findAllByIds(clientIds)));
            } else {
                campaign.setClientes(new HashSet<>()); // Limpia los clientes si se elimina todo
            }

            campaignServiceImpl.update(campaign);
            redirectAttributes.addFlashAttribute("mensaje", "Campaña actualizada exitosamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar campaña: " + e.getMessage());
        }

        return "redirect:/campaigns/administration";
    }

    @PostMapping("/delete")
    public String deleteCampaign(@RequestParam Long id, RedirectAttributes redirectAttributes){
        campaignServiceImpl.deleteById(id);
        redirectAttributes.addFlashAttribute("mensaje", "Campaña eliminada exitosamente");
        return "redirect:/campaigns/administration";
    }

}