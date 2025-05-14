package com.xcrm.controller.web;

import com.xcrm.model.*;
import com.xcrm.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/sales")
public class SalesController {

    private final CampaignService campaignService;
    private final UserService userService;
    private final OrganizationService organizationService;
    private final ClientService clientService;
    private final InteractionService interactionService;
    private final VentaService ventaService;

    public SalesController(CampaignService campaignService, UserService userService, OrganizationService organizationService, ClientService clientService, InteractionService interactionService, VentaService ventaService){
        this.campaignService = campaignService;
        this.userService = userService;
        this.organizationService = organizationService;
        this.clientService = clientService;
        this.interactionService = interactionService;
        this.ventaService = ventaService;
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
                            .filter(interaccion -> interaccion.getCampaign().getId().equals(campaignId)) // Filtrar por campaña seleccionada
                            .max(Comparator.comparing(Interaccion::getFechaHora)) // Obtener la más reciente
                            .orElse(null)
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

    @GetMapping("/interaction/{id}")
    public String newInteraction(@PathVariable Long id, @RequestParam (name = "campaignId") Long campaignId, Model model){
        model.addAttribute("client",clientService.findById(id).get());
        model.addAttribute("selectedCampaign", campaignService.findById(campaignId).get());
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
        Client client = clientService.findById(clientId).get();
        interaction.setClient(client);

        // Obtener la campaña y asignarla a la interacción
        Campaign campaign = campaignService.findById(campaignId).orElse(null);  // Obtener campaña
        if (campaign != null) {
            interaction.setCampaign(campaign);  // Asigno la campaña
        }

        // Obtener el usuario autenticado (comercial)
        String username = authentication.getName();
        User comercial = userService.findByUsername(username);
        interaction.setComercial(comercial);

        interaction.setFechaHora(LocalDateTime.now());

        Interaccion savedInteraction = interactionService.save(interaction);

        // Si se marcó como venta
        if (crearVenta != null && crearVenta) {
            Venta venta = new Venta();
            venta.setFecha(LocalDate.now());
            venta.setInteraccion(savedInteraction);
            venta.setMonto(BigDecimal.valueOf(200.00));
            venta.setProducto("prueba controller");
            ventaService.save(venta);
        }
        redirectAttributes.addFlashAttribute("mensaje", "Interación registrada de : " + client.getNombre());
        return "redirect:/sales";
    }

}
