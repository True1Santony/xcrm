package com.xcrm.controller.web;

import com.xcrm.model.Campaign;
import com.xcrm.model.Client;
import com.xcrm.model.User;
import com.xcrm.service.CampaignService;
import com.xcrm.service.ClientService;
import com.xcrm.service.OrganizationService;
import com.xcrm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    @GetMapping
    public String showClientsAdministrationDashboard(Authentication authentication, Model model){
        model.addAttribute("organization", organizationService.getCurrentOrganization());
        model.addAttribute("clients", clientService.findAll());
        model.addAttribute("campaigns", campaignService.findAll());
        model.addAttribute("comerciales", userService.findAll());
        model.addAttribute("new_client", new Client());

        return "client-administration-dashboard";
    }

    @PostMapping
    public String createClient(
            @Valid @ModelAttribute("new_client") Client client,
            BindingResult bindingResult,
            @RequestParam(value = "campaignIds", required = false) List<Long> campaignIds,
            @RequestParam(value = "comercialIds", required = false) List<UUID> salesRepresentativesIds,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            // Pasar todos los errores al modelo
            bindingResult.getFieldErrors().forEach(
                    error -> redirectAttributes.addFlashAttribute(
                            "error_" + error.getField(), error.getDefaultMessage()
                    ));
            redirectAttributes.addFlashAttribute("error", "Corrija los errores del formulario");
            redirectAttributes.addFlashAttribute("new_client", client); // Mantener los datos ingresados
            return "redirect:/clients";
        }

        try {
            // Guardar el cliente y sus relaciones
            Client savedClient = clientService.createClient(client, campaignIds, salesRepresentativesIds);
            redirectAttributes.addFlashAttribute("mensaje", "Cliente " + savedClient.getNombre() + " creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el cliente: " + e.getMessage());
        }

        return "redirect:/clients";
    }

    @PostMapping("/delete")
    public String deleteClient(@RequestParam Long id){
        clientService.deleteById(id);
        return "redirect:/clients";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        Client client = clientService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        
        List<Campaign> allCampaigns = campaignService.findAll();
        List<User> allComerciales = userService.findAll();

        model.addAttribute("client", client);
        model.addAttribute("allCampaigns", allCampaigns);
        model.addAttribute("allComerciales", allComerciales);

        return "client-edit";
    }

    @PostMapping("/update")
    public String updateClient(@ModelAttribute("client") Client incomingClient,
                               @RequestParam(value = "campaignIds", required = false) List<Long> campaignIds,
                               @RequestParam(value = "comercialIds", required = false) UUID[] salesRepresentativesIds,
                               RedirectAttributes redirectAttributes) {

        try {
            clientService.updateClient(incomingClient, campaignIds, salesRepresentativesIds);
            redirectAttributes.addFlashAttribute("mensaje", "Cliente actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el cliente: " + e.getMessage());
        }

        return "redirect:/clients";
    }

}
