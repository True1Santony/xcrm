package com.xcrm.controller.web;

import com.xcrm.dto.EditarFotoDTO;
import com.xcrm.model.Organization;
import com.xcrm.model.User;
import com.xcrm.service.*;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
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

import java.security.Principal;
import java.util.UUID;

@Log4j2
@Controller
@RequestMapping("/usuarios")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private EmailSender emailSender;

    @Value("${uploads.dir}")
    private String uploadDir;

    @ModelAttribute("titulo")
    public String title() {
        return "Registro de Usuarios";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@Valid @ModelAttribute("nuvoUsuario") User nuevoUsuario,
                                   @RequestParam Long organizacionId, BindingResult almacenErrores,
                                   Model model, RedirectAttributes redirectAttributes) {

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
        model.addAttribute("organization", organizationService.getCurrentOrganization());
        model.addAttribute("nuevoUsuario", new User());
        return "user-administration-dashboard";
    }

    @PostMapping("/editar")
    public String editarUsuario(@RequestParam("user_id") UUID userId,
                                @RequestParam("nuevoUsername") String nuevoUsername,
                                @RequestParam(value = "roles", required = false) String[] roles,
                                @RequestParam(value = "nuevoPassword", required = false) String nuevoPassword,
                                RedirectAttributes redirectAttributes) {

        userService.actualizarUsuario(userId, nuevoUsername, nuevoPassword, roles);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
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

        // Crear el DTO y asignar los datos correspondientes
        EditarFotoDTO dto = new EditarFotoDTO();
        dto.setId(actual.getId());
        dto.setOrganizacionId(actual.getOrganizacion().getId());
        dto.setFotoUrl(actual.getFotoUrl());
        dto.setCompania(actual.getOrganizacion().getNombre());
        dto.setNombre(actual.getUsername());
        dto.setOrganizacion(actual.getOrganizacion());

        model.addAttribute("usuario", dto);
        return "edit-mi-perfil";
    }

//refact
    @PostMapping("/edit-mi-perfil/update")
    public String actualizarFoto(@ModelAttribute("usuario") EditarFotoDTO dto,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam(value = "foto", required = false) MultipartFile foto,
                                 RedirectAttributes redirectAttributes) {

        log.info("== Guardando nueva foto de perfil ==");
        log.info("Archivo recibido: " + (foto != null ? foto.getOriginalFilename() : "NULO"));

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
                log.info("Ruta pública guardada: " + rutaPublica);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al guardar la imagen: " + e.getMessage());
                return "redirect:/mi-cuenta";
            }
        }

        userService.save(actual);
        redirectAttributes.addFlashAttribute("success", "Foto de perfil actualizada correctamente.");
        return "redirect:/mi-cuenta";
    }

    @GetMapping("/configuracion")
    public String mostrarConfiguracion(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User actual = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("usuario", actual);
        return "configuracion";
    }

    @GetMapping("/usuarios/configuracion")
    public String mostrarConfiguracion(Model model, Principal principal) {
        User usuario = userService.findByUsername(principal.getName());
        model.addAttribute("usuario", usuario);
        return "configuracion";
    }

    //ojo rompe multitenant
    @PostMapping("/actualizar-configuracion")
    public String actualizarConfiguracion(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestParam("compania") String compania,
                                          @RequestParam("nombre") String nombre,
                                          @RequestParam("correo") String correo,
                                          @RequestParam(value = "password", required = false) String password,
                                          RedirectAttributes redirectAttributes) {

        User actual = userService.findByUsername(userDetails.getUsername());

        if (nombre == null || nombre.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El nombre del usuario no puede estar vacío.");
            return "redirect:/usuarios/configuracion";
        }

        if (correo == null || !correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            redirectAttributes.addFlashAttribute("error", "Debes proporcionar un correo válido.");
            return "redirect:/usuarios/configuracion";
        }

        if (compania == null || compania.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El nombre de la compañía no puede estar vacío.");
            return "redirect:/usuarios/configuracion";
        }

        // Actualización de datos
        actual.setUsername(nombre);

        if (password != null && !password.trim().isEmpty()) {
            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
                return "redirect:/usuarios/configuracion";
            }

            actual.setPassword(password); // ⚠️ hay que cifrar con Spring Security, si
        }

        Organization org = actual.getOrganizacion();
        org.setNombre(compania);
        organizationService.save(org); // solo se actualiza en su bd, no en central
        userService.save(actual);

        redirectAttributes.addFlashAttribute("success", "Los datos de configuración fueron actualizados correctamente.");
        return "redirect:/usuarios/configuracion";
    }

    @PostMapping("/guardar-foto")
    public String guardarFotoPerfil(@RequestParam("usuarioId") UUID userId,
                                    @RequestParam("foto") MultipartFile foto,
                                    RedirectAttributes redirectAttributes,
                                    @AuthenticationPrincipal UserDetails userDetails) {

        User actual = userService.findByUsername(userDetails.getUsername());

        if (!actual.getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para modificar otro perfil.");
            return "redirect:/usuarios/edit-mi-perfil";
        }

        if (foto == null || foto.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar una imagen.");
            return "redirect:/usuarios/edit-mi-perfil";
        }

        if (!foto.getContentType().startsWith("image/")) {
            redirectAttributes.addFlashAttribute("error", "Solo se permiten archivos de imagen.");
            return "redirect:/usuarios/edit-mi-perfil";
        }

        if (foto.getSize() > (2 * 1024 * 1024)) {
            redirectAttributes.addFlashAttribute("error", "La imagen debe pesar menos de 2MB.");
            return "redirect:/usuarios/edit-mi-perfil";
        }

        try {
            String originalNombre = foto.getOriginalFilename();
            String extension = originalNombre != null && originalNombre.contains(".")
                    ? originalNombre.substring(originalNombre.lastIndexOf("."))
                    : "";
            String nombreArchivo = actual.getId() + extension;

            String rutaPublica = imageService.guardarImagen(foto, nombreArchivo);
            actual.setFotoUrl(rutaPublica);

            userService.save(actual);
            redirectAttributes.addFlashAttribute("success", "Foto de perfil actualizada correctamente.");

        } catch (Exception e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al guardar la imagen: " + e.getMessage());
        }

        return "redirect:/usuarios/edit-mi-perfil";
    }
    @PostMapping("/enviar-ticket-edicion")
    public String enviarTicketEdicion(
            @RequestParam("usuarioId") UUID usuarioId,
            @RequestParam("organizacionId") Long organizacionId,
            @RequestParam("compania") String compania,
            @RequestParam("nombre") String nombre,
            @RequestParam("correo") String correo,
            @RequestParam("plan") String plan,
            @RequestParam("motivo") String motivo,
            RedirectAttributes redirectAttributes) {

        String asunto = "Solicitud de Edición de Perfil - " + nombre;
        String mensaje = String.format(
                "Nombre del Usuario: %s\n" +
                        "Correo Electrónico: %s\n" +
                        "Compañía: %s\n" +
                        "Plan Actual: %s\n" +
                        "--------------------------------\n" +
                        "Motivo de la solicitud:\n%s",
                nombre, correo, compania, plan, motivo
        );

        try {
            emailSender.enviarCorreo(nombre, correo, asunto, mensaje, null, null);
            redirectAttributes.addFlashAttribute("success", "El ticket fue enviado correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al enviar el ticket: " + e.getMessage());
        }

        return "redirect:/usuarios/edit-mi-perfil";
    }
}