package com.xcrm.controller.web;

import com.xcrm.model.Organizacion;
import com.xcrm.model.User;
import com.xcrm.service.OrganizacionService;
import com.xcrm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/usuarios")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private OrganizacionService organizacionService;

    @ModelAttribute("titulo")
    public String title() {
        return "Registro de Usuarios";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@Valid
                                       @ModelAttribute("nuvoUsuario") User nuevoUsuario,
                                   @RequestParam Long organizacionId, BindingResult almacenErrores,
                                   Model model) {

        // Buscar la organización por ID
        Organizacion organizacion = organizacionService.buscarPorId(organizacionId);

        if (organizacion == null) {
            model.addAttribute("error", "La organización no existe.");
            return "mi-cuenta"; // Regresar a la vista
        }

        try {
            userService.createUserInOrganization(nuevoUsuario.getUsername(), nuevoUsuario.getPassword(), organizacion);
            model.addAttribute("success", "Usuario registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("loggedIn", true);

        return "redirect:/mi-cuenta";
    }
}
