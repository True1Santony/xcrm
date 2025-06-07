package com.xcrm.controller.web;

import com.xcrm.DTO.EditarFotoDTO;
import com.xcrm.model.Organization;
import com.xcrm.model.User;
import com.xcrm.service.*;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    // Servicios necesarios para operaciones con usuarios, organizaciones, imágenes, envío de emails Y contraseña codificada
    @Autowired private UserService userService;
    @Autowired private OrganizationService organizationService;
    @Autowired private ImageService imageService;
    @Autowired private EmailSender emailSender;
    @Autowired private PasswordEncoder passwordEncoder;


    @Value("${uploads.dir}")
    private String uploadDir;

    // Atributo común para mostrar título en las vistas relacionadas con usuarios
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
        redirectAttributes.addFlashAttribute("success", "Usuario actualizado correctamente");
        return "redirect:/usuarios/administration";
    }

    @PostMapping("/eliminar")
    public String eliminarUsuario(@RequestParam("user_id") UUID userId, RedirectAttributes redirectAttributes) {
        userService.eliminarUsuario(userId);
        redirectAttributes.addFlashAttribute("success", "Usuario eliminado correctamente");
        return "redirect:/usuarios/administration";
    }

    // Muestra el formulario para que el usuario autenticado pueda editar su perfil, cargando sus datos actuales.
    @GetMapping("/edit-mi-perfil")
    public String mostrarFormularioEditarPerfil(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User usuarioActual = userService.findByUsername(userDetails.getUsername());

        EditarFotoDTO perfilDTO = new EditarFotoDTO();
        perfilDTO.setId(usuarioActual.getId());
        perfilDTO.setFotoUrl(usuarioActual.getFotoUrl());
        perfilDTO.setCompania(usuarioActual.getOrganizacion().getNombre());
        perfilDTO.setNombre(usuarioActual.getUsername());
        perfilDTO.setOrganizacion(usuarioActual.getOrganizacion());

        model.addAttribute("usuario", perfilDTO);
        return "edit-mi-perfil";
    }

    /**
     * Actualiza la foto de perfil del usuario autenticado.
     * Valida la imagen, la guarda y actualiza la URL en el perfil.
     */
    @PostMapping("/edit-mi-perfil/update")
    public String actualizarFotoPerfil(@ModelAttribute("usuario") EditarFotoDTO perfilDTO,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       @RequestParam(value = "foto", required = false) MultipartFile foto,
                                       RedirectAttributes redirectAttributes) {

        log.info("== Guardando nueva foto de perfil ==");
        log.info("Archivo recibido: " + (foto != null ? foto.getOriginalFilename() : "NULO"));

        User usuarioActual = userService.findByUsername(userDetails.getUsername());

        if (!perfilDTO.getId().equals(usuarioActual.getId())) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para modificar otro perfil.");
            return "redirect:/mi-cuenta";
        }

        if (foto != null && !foto.isEmpty()) {
            try {
                validarImagen(foto, redirectAttributes, "redirect:/mi-cuenta");

                String extension = obtenerExtensionArchivo(foto.getOriginalFilename());
                String nombreArchivo = usuarioActual.getId() + extension;
                String rutaPublica = imageService.saveImage(foto, nombreArchivo);
                usuarioActual.setFotoUrl(rutaPublica);
                log.info("Ruta pública guardada: " + rutaPublica);
            } catch (IllegalArgumentException e) {
                // Ya seteó el mensaje de error en redirectAttributes dentro de validarImagen
                return "redirect:/mi-cuenta";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al guardar la imagen: " + e.getMessage());
                return "redirect:/mi-cuenta";
            }
        }

        userService.save(usuarioActual);
        redirectAttributes.addFlashAttribute("success", "Foto de perfil actualizada correctamente.");
        return "redirect:/mi-cuenta";
    }

    // Muestra la configuración del usuario actual para poder modificar datos personales y de la organización.
    @GetMapping("/configuracion")
    public String mostrarConfiguracion(Model model, Principal principal) {
        User usuario = userService.findByUsername(principal.getName());
        model.addAttribute("usuario", usuario);
        return "configuracion";
    }

    /**
     * ️ ⚠️ ojo rompe multitenant
     * Actualiza la configuración del usuario y su organización.
     * Realiza validaciones básicas y guarda cambios, pero tiene una advertencia sobre multitenancy.
     */
    @PostMapping("/actualizar-configuracion")
    public String actualizarConfiguracionUsuario(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestParam("compania") String compania,
                                                 @RequestParam("nombre") String nombre,
                                                 @RequestParam("correo") String correo,
                                                 @RequestParam(value = "password", required = false) String password,
                                                 RedirectAttributes redirectAttributes) {

        User usuarioActual = userService.findByUsername(userDetails.getUsername());

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

        usuarioActual.setUsername(nombre);

        if (password != null && !password.trim().isEmpty()) {
            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
                return "redirect:/usuarios/configuracion";
            }

            // Codificar la contraseña antes de asignarla
            usuarioActual.setPassword(passwordEncoder.encode(password));
        }

        Organization organizacion = usuarioActual.getOrganizacion();
        organizacion.setNombre(compania);
        organizationService.save(organizacion); // Se guarda solo en BD del tenant
        userService.save(usuarioActual);

        redirectAttributes.addFlashAttribute("success", "Los datos de configuración fueron actualizados correctamente.");
        return "redirect:/usuarios/configuracion";
    }

    // Permite guardar la foto de perfil desde un formulario, validando que el usuario solo modifique su propio perfil.
    @PostMapping("/guardar-foto")
    public String guardarFotoPerfilDesdeFormulario(@RequestParam("usuarioId") UUID userId,
                                                   @RequestParam("foto") MultipartFile foto,
                                                   RedirectAttributes redirectAttributes,
                                                   @AuthenticationPrincipal UserDetails userDetails) {

        User usuarioActual = userService.findByUsername(userDetails.getUsername());

        if (!usuarioActual.getId().equals(userId)) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para modificar otro perfil.");
            return "redirect:/usuarios/edit-mi-perfil";
        }

        try {
            String extension = obtenerExtensionArchivo(foto.getOriginalFilename());
            String nombreArchivo = usuarioActual.getId() + extension;
            String rutaPublica = imageService.saveImage(foto, nombreArchivo);
            usuarioActual.setFotoUrl(rutaPublica);
            userService.save(usuarioActual);
            redirectAttributes.addFlashAttribute("success", "Foto de perfil actualizada correctamente.");
        } catch (Exception e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al guardar la imagen: " + e.getMessage());
        }

        return "redirect:/usuarios/edit-mi-perfil";
    }

    /**
     * Envía un ticket o solicitud por email para pedir edición de perfil.
     * Construye el mensaje con datos del usuario y el motivo, y maneja errores en el envío.
     */
    @PostMapping("/enviar-ticket-edicion")
    public String enviarSolicitudEdicionPerfil(@RequestParam("usuarioId") UUID usuarioId,
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

    /**
     * Valida que la imagen recibida no esté vacía, sea de tipo imagen y no supere los 2MB.
     * En caso de error, establece mensajes y lanza excepción para abortar el proceso.
     */
    private void validarImagen(MultipartFile foto, RedirectAttributes redirectAttributes, String redirectPath) throws IllegalArgumentException {
        if (foto == null || foto.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar una imagen.");
            throw new IllegalArgumentException("Debe seleccionar una imagen.");
        }

        if (!foto.getContentType().startsWith("image/")) {
            redirectAttributes.addFlashAttribute("error", "Solo se permiten archivos de imagen.");
            throw new IllegalArgumentException("Solo se permiten archivos de imagen.");
        }

        if (foto.getSize() > (2 * 1024 * 1024)) {
            redirectAttributes.addFlashAttribute("error", "La imagen debe pesar menos de 2MB.");
            throw new IllegalArgumentException("La imagen debe pesar menos de 2MB.");
        }
    }

    /**
     * Helper privado
     * Método auxiliar para obtener la extensión del archivo a partir de su nombre original.
     */
    private String obtenerExtensionArchivo(String nombreOriginal) {
        return (nombreOriginal != null && nombreOriginal.contains("."))
                ? nombreOriginal.substring(nombreOriginal.lastIndexOf("."))
                : "";
    }
}