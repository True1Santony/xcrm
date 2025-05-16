package com.xcrm.controller.web;

import com.xcrm.dto.EditarFotoDTO;
import com.xcrm.model.Organization;
import com.xcrm.model.User;
import com.xcrm.service.ImageService;
import com.xcrm.service.OrganizationService;
import com.xcrm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.UUID;

@Controller
@RequestMapping("/usuarios")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ImageService imageService;

    // Ruta configurable para almacenamiento externo
    @Value("${uploads.dir}")
    private String uploadDir;

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

    @GetMapping("/edit-mi-perfil")
    public String editarMiPerfil(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User actual = userService.findByUsername(userDetails.getUsername());

        model.addAttribute("usuario",EditarFotoDTO.builder()
                                            .fotoUrl(actual.getFotoUrl())
                                            .id(actual.getId())
                                            .organizacionId(actual.getOrganizacion().getId()));
        return "edit-mi-perfil";
}

    @PostMapping("/edit-mi-perfil/update")
    public String actualizarFoto(@ModelAttribute("usuario") EditarFotoDTO dto,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam(value = "foto", required = false) MultipartFile foto,
                                 RedirectAttributes redirectAttributes) {

        System.out.println("== Guardando nueva foto de perfil ==");
        System.out.println("Archivo recibido: " + (foto != null ? foto.getOriginalFilename() : "NULO"));

        User actual = userService.findByUsername(userDetails.getUsername());

        if (!dto.getId().equals(actual.getId())) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para modificar otro perfil.");
            return "redirect:/mi-cuenta";
        }

        if (foto != null && !foto.isEmpty()) {
            if (!foto.getContentType().startsWith("image/")) {
                redirectAttributes.addFlashAttribute("error", "Solo se permiten archivos de imagen.");
                return "redirect:/mi-cuenta";
            }

            if (foto.getSize() > (2 * 1024 * 1024)) {
                redirectAttributes.addFlashAttribute("error", "La imagen debe pesar menos de 2MB.");
                return "redirect:/mi-cuenta";
            }

            try {
                String originalNombre = foto.getOriginalFilename();
                String extension = originalNombre != null && originalNombre.contains(".")
                        ? originalNombre.substring(originalNombre.lastIndexOf("."))
                        : "";
                String nombreArchivo = actual.getId() + extension;

                String rutaPublica = imageService.guardarImagen(foto, nombreArchivo);

                actual.setFotoUrl(rutaPublica);
                System.out.println("Ruta pública guardada: " + rutaPublica);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al guardar la imagen: " + e.getMessage());
                return "redirect:/mi-cuenta";
            }
        }

        userService.save(actual);
        redirectAttributes.addFlashAttribute("success", "Foto de perfil actualizada correctamente.");
        return "redirect:/mi-cuenta";
    }
}