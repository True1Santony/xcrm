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

    // Servicios necesarios para gestionar campañas, organizaciones y clientes
    private final CampaignService campaignService;
    private final OrganizationService organizationService;
    private final ClientService clientService;

    /**
     * Muestra el dashboard de administración de campañas.
     * Carga campañas existentes, clientes y una nueva campaña vacía para el formulario.
     */
    @GetMapping("/administration")
    public String getCampaignsAdministrationDashboard(Model model) {
        populateAdministrationModel(model);
        model.addAttribute("newCampaign", new Campaign());
        return "campaigns-administration-dashboard";
    }

    /**
     * Procesa la creación de una nueva campaña.
     * Valida fechas, asocia clientes seleccionados y guarda la campaña.
     */
    @PostMapping
    public String createCampaign(
            @ModelAttribute("newCampaign") Campaign campaign,
            @RequestParam(value = "clientes", required = false) List<Long> clientIds,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            // Si hay errores de validación, se recarga el formulario con los datos
            populateAdministrationModel(model);
            return "campaigns-administration-dashboard";
        }

        try {
            // Asocia la campaña a la organización actual
            campaign.setOrganizacion(organizationService.getCurrentOrganization());

            // Validación de fechas: fecha fin no puede ser anterior a la de inicio
            if (campaign.getFechaFin().isBefore(campaign.getFechaInicio())) {
                redirectAttributes.addFlashAttribute("error", "La fecha de fin no puede ser anterior a la fecha de inicio.");
                return "redirect:/campaigns/administration";
            }

            // Asocia los clientes seleccionados a la campaña (si hay)
            if (clientIds != null && !clientIds.isEmpty()) {
                campaign.setClientes(new HashSet<>(clientService.findAllByIds(clientIds)));
            }

            // Guarda la campaña en la base de datos
            campaignService.save(campaign);
            redirectAttributes.addFlashAttribute("mensaje", "Campaña creada exitosamente");

        } catch (Exception e) {
            // Manejo genérico de errores
            redirectAttributes.addFlashAttribute("error", "Error al crear campaña: " + e.getMessage());
        }

        return "redirect:/campaigns/administration";
    }

    /**
     * Muestra el formulario de edición de una campaña específica.
     * Solo permite editar si la campaña aún no ha comenzado.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Optional<Campaign> optionalCampaign = campaignService.findById(id);

        if (optionalCampaign.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Campaña no encontrada");
            return "redirect:/campaigns/administration";
        }

        Campaign campaign = optionalCampaign.get();

        // No se permite editar campañas que ya comenzaron
        if (campaign.getFechaInicio().isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "No se puede editar una campaña que ya ha comenzado.");
            return "redirect:/campaigns/administration";
        }

        // Se carga la campaña y la lista de clientes para el formulario de edición
        model.addAttribute("campaign", campaign);
        model.addAttribute("allClients", clientService.findAll());

        return "campaign-edit";
    }

    /**
     * Procesa la actualización de los datos de una campaña existente.
     * Solo permite editar campañas que aún no han comenzado.
     */
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
            return "redirect:/campaigns/administration";
        }

        Campaign campaign = optionalCampaign.get();

        // Validación de que la campaña no haya iniciado
        if (campaign.getFechaInicio().isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "No se puede editar una campaña que ya ha comenzado.");
            return "redirect:/campaigns/administration";
        }

        try {
            // Se actualizan los campos de la campaña
            campaign.setNombre(nombre);
            campaign.setDescripcion(descripcion);
            campaign.setFechaInicio(LocalDate.parse(fechaInicio));
            campaign.setFechaFin(LocalDate.parse(fechaFin));
            campaign.setOrganizacion(organizationService.getCurrentOrganization());

            // Se actualiza la asociación con los clientes
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

    // Elimina una campaña existente por su ID.
    @PostMapping("/delete")
    public String deleteCampaign(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        campaignService.deleteById(id);
        redirectAttributes.addFlashAttribute("mensaje", "Campaña eliminada exitosamente");
        return "redirect:/campaigns/administration";
    }

    /**
     * Método reutilizado para cargar datos comunes en el dashboard de administración:
     * organización actual, campañas y lista de clientes.
     */
    private void populateAdministrationModel(Model model) {
        model.addAttribute("organization", organizationService.getCurrentOrganization());
        model.addAttribute("campaigns", campaignService.findAll());
        model.addAttribute("allClients", clientService.findAll());
    }
}