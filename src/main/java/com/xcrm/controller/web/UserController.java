package com.xcrm.controller.web;

import com.xcrm.model.Organization;
import com.xcrm.model.User;
import com.xcrm.service.OrganizationService;
import com.xcrm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/usuarios")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    @ModelAttribute("titulo")
    public String title() {
        return "Registro de Usuarios";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@Valid
                                       @ModelAttribute("nuvoUsuario") User nuevoUsuario,
                                   @RequestParam Long organizacionId, BindingResult almacenErrores,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        Organization organization = organizationService.findById(organizacionId).get();

        try {
            userService.createUserInOrganization(nuevoUsuario.getUsername(), nuevoUsuario.getPassword(), organization);
            redirectAttributes.addFlashAttribute("success", "Usuario registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios/administration";
    }

    @GetMapping("/administration")
    public String getUserAdministrationDashboard(Model model){
        model.addAttribute("organization", organizationService.getCurrentOrganization()); // Paso la organización activa
        model.addAttribute("nuevoUsuario", new User());
        return "user-administration-dashboard";
    }

    @PostMapping("/editar")
    public String editarUsuario(@RequestParam("user_id") UUID userId,
                                @RequestParam("nuevoUsername") String nuevoUsername,
                                @RequestParam(value = "roles", required = false) String[] roles,
                                @RequestParam(value = "nuevoPassword", required = false) String nuevoPassword,
                                Model model) {

        userService.actualizarUsuario(userId, nuevoUsername, nuevoPassword, roles);

        model.addAttribute("mensaje", "Usuario actualizado correctamente");
        return "redirect:/usuarios/administration";
    }

    @PostMapping("/eliminar")
    public String eliminarUsuario(@RequestParam("user_id") UUID userId, RedirectAttributes redirectAttributes) {
        userService.eliminarUsuario(userId);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
        return "redirect:/usuarios/administration";
    }
}
