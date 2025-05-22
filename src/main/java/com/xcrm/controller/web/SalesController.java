package com.xcrm.controller.web;

import com.xcrm.model.*;
import com.xcrm.service.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@Controller
@RequestMapping("/sales")
public class SalesController {

    private final CampaignServiceImpl campaignServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final OrganizationServiceImpl organizationServiceImpl;
    private final ClientServiceImpl clientServiceImpl;
    private final InteractionServiceImpl interactionServiceImpl;
    private final VentaServiceImpl ventaServiceImpl;

    @GetMapping
    public String getSalesAdministrationDashboard(Authentication authentication, Model model){
        String username = authentication.getName();
        User user = userServiceImpl.findByUsername(username);

        model.addAttribute("allComerciales", userServiceImpl.findAll());
        model.addAttribute("allCampaigns", campaignServiceImpl.findAll());
        model.addAttribute("organization", organizationServiceImpl.getCurrentOrganization());
        model.addAttribute("campaignsForUser", (user.getCampaigns()));
        return "sales-administration-dashboard";
    }

    @PostMapping("/assign")
    public String assignCampaignsToUser(@RequestParam("userId") UUID userId,
                                        @RequestParam("campaignIds") List<Long> campaignIds,
                                        RedirectAttributes redirectAttributes) {
        User user = userServiceImpl.findById(userId);
        Set<Campaign> selectedCampaigns = new HashSet<>(campaignServiceImpl.findAllByIds(campaignIds));
        user.setCampaigns(selectedCampaigns);
        userServiceImpl.save(user);
        redirectAttributes.addFlashAttribute("mensaje", "Campañas asignadas correctamente a " + user.getUsername());
        return "redirect:/sales";
    }

    @GetMapping("/clientByIdCampaign")
    public String getClientsByCampaign(@RequestParam(name = "campaignId") Long campaignId,
                                       Authentication authentication, Model model){
        String username = authentication.getName();
        User user = userServiceImpl.findByUsername(username);

        Campaign selectedCampaign = campaignServiceImpl.findById(campaignId).orElse(null);

        Set<Client> clients = selectedCampaign.getClientes();

        clients.forEach(client -> {
            client.setLastInteraction(
                    client.getInteracciones().stream()
                            .filter(interaccion -> interaccion.getCampaign().getId().equals(campaignId)) // Filtrar por campaña seleccionada
                            .max(Comparator.comparing(Interaccion::getFechaHora)) // Obtener la más reciente
                            .orElse(null)
            );
        });

        model.addAttribute("allComerciales", userServiceImpl.findAll());
        model.addAttribute("allCampaigns", campaignServiceImpl.findAll());
        model.addAttribute("organization", organizationServiceImpl.getCurrentOrganization());
        model.addAttribute("campaignsForUser", (user.getCampaigns()));
        model.addAttribute("selectedCampaign", selectedCampaign);
        model.addAttribute("clients", clients);
        return "sales-administration-dashboard";
    }

    @GetMapping("/interaction/{id}")
    public String newInteraction(@PathVariable Long id, @RequestParam (name = "campaignId") Long campaignId, Model model){
        model.addAttribute("client", clientServiceImpl.findById(id).get());
        model.addAttribute("selectedCampaign", campaignServiceImpl.findById(campaignId).get());
        model.addAttribute("newInteraction", new Interaccion());

        return "new-iteraction";
    }

    @PostMapping("/interaction/save")
    public String saveInteraction(@ModelAttribute("newInteraction") Interaccion interaction,
                                  @RequestParam Long clientId,
                                  @RequestParam(required = false) Boolean crearVenta,
                                  @RequestParam Long campaignId,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        // Obtener el cliente, controlar el null
        Client client = clientServiceImpl.findById(clientId).get();
        interaction.setClient(client);

        // Obtener la campaña y asignarla a la interacción
        Campaign campaign = campaignServiceImpl.findById(campaignId).orElse(null);  // Obtener campaña
        if (campaign != null) {
            interaction.setCampaign(campaign);  // Asigno la campaña
        }

        // Obtener el usuario autenticado (comercial)
        String username = authentication.getName();
        User comercial = userServiceImpl.findByUsername(username);
        interaction.setComercial(comercial);

        interaction.setFechaHora(LocalDateTime.now());

        Interaccion savedInteraction = interactionServiceImpl.save(interaction);

        // Si se marcó como venta
        if (crearVenta != null && crearVenta) {
            Venta venta = new Venta();
            venta.setFecha(LocalDate.now());
            venta.setInteraccion(savedInteraction);
            venta.setMonto(BigDecimal.valueOf(200.00));
            venta.setProducto("prueba controller");
            ventaServiceImpl.save(venta);
        }
        redirectAttributes.addFlashAttribute("mensaje", "Interación registrada de : " + client.getNombre());
        return "redirect:/sales";
    }
}
