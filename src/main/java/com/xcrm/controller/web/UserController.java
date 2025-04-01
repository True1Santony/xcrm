package com.xcrm.controller.web;

import com.xcrm.model.Authority;
import com.xcrm.model.Organizacion;
import com.xcrm.model.User;
import com.xcrm.repository.AuthorityRepository;
import com.xcrm.service.AuthorityService;
import com.xcrm.service.OrganizacionService;
import com.xcrm.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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

        return "redirect:/usuarios/administration";
    }

    @GetMapping("/administration")
    public String getUserAdministrationDashboard(Model model){
        //esto se repite en indexController mi-cuenta, encapsular en un metodo de organizacion
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        //obtengo de la Base de datos la Organizacion a partir del username
        Optional<Organizacion> organizacionOptional = organizacionService
                .obtenerPorId(userService
                        .obtenerUsuarioPorNombre(username)
                        .getOrganizacion()
                        .getId());

        model.addAttribute("organizacion", organizacionOptional.get()); // Paso la organización activa
        model.addAttribute("nuevoUsuario", new User());

        return "user-administration-dashboard";
    }

    // Método para editar un usuario
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
    public String eliminarUsuario(@RequestParam("user_id") UUID userId, Model model) {
        userService.eliminarUsuario(userId);
        model.addAttribute("mensaje", "Usuario eliminado correctamente");
        return "redirect:/usuarios/administration";
    }
}
