package com.xcrm.controller.web;

import com.xcrm.model.ContactoMensaje;
import com.xcrm.model.User;
import com.xcrm.repository.ContactoMensajeRepository;
import com.xcrm.service.ContactoMensajeService;
import com.xcrm.service.EmailSender;
import com.xcrm.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.UUID;

@Log4j2
@Controller
public class ContactoController {
    @Autowired
    private EmailSender emailSender;

    @Autowired
    private ContactoMensajeService contactoMensajeService;

    @Autowired
    private UserService userService;

    @Value("${dropbox.app.key}")
    private String dropboxAppKey;

    @GetMapping("/contacto")
    public String mostrarFormularioContacto(Model model) {
        model.addAttribute("dropboxKey", dropboxAppKey);
        model.addAttribute("titulo", "Contacto de XCRM");
        return "contacto";
    }

    @PostMapping("/enviar-contacto")
    public String enviarContacto(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String asunto,
            @RequestParam String mensaje,
            @RequestPart(required = false) MultipartFile archivo,
            @RequestParam(required = false) String archivoDropbox,
            RedirectAttributes redirectAttributes) {

        String logArchivo = "";

        // Si el archivo fue adjuntado, registra la información del archivo local
        if (archivo != null && !archivo.isEmpty()) {
            logArchivo += String.format("📎 archivo local: '%s' (%d KB)", archivo.getOriginalFilename(), archivo.getSize() / 1024);
        } else {
            logArchivo += "📎 archivo local: no adjunto";
        }

        // Si hay un enlace de Dropbox, lo registra
        if (archivoDropbox != null && !archivoDropbox.trim().isEmpty()) {
            logArchivo += String.format(", 🌐 Dropbox: %s", archivoDropbox);
        } else {
            logArchivo += ", 🌐 Dropbox: no adjunto";
        }

        log.info("Formulario recibido: nombre='{}', email='{}', asunto='{}', mensaje='{}', {}",
                nombre, email, asunto, mensaje, logArchivo);

        try {

            emailSender.enviarCorreo(nombre, email, asunto, mensaje, archivo, archivoDropbox);

            // Crear entidad
            ContactoMensaje contacto = new ContactoMensaje();
            contacto.setNombre(nombre);
            contacto.setEmail(email);
            contacto.setAsunto(asunto);
            contacto.setMensaje(mensaje);
            contacto.setArchivoDropboxUrl(archivoDropbox);

            // Si se ha adjuntado un archivo, se guarda su nombre
            if (archivo != null && !archivo.isEmpty()) {
                contacto.setArchivoNombre(archivo.getOriginalFilename());
            }

            // Si el usuario está logueado, guarda su UUID en el mensaje
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
                String username = auth.getName();
                User user = userService.findByUsername(username);
                if (user != null) {
                    contacto.setUsuarioId(user.getId().toString());  // Asigna el UUID del usuario logueado
                }
            } else {
                UUID anonId = UUID.randomUUID();  // UUID aleatorio para anónimos
                contacto.setUsuarioId(anonId.toString());
            }

            // Guarda el mensaje en la base de datos
            contactoMensajeService.guardarMensaje(contacto);
            redirectAttributes.addFlashAttribute("success", "¡Mensaje enviado correctamente!");
        } catch (IllegalArgumentException e) {
            log.warn("Validación fallida: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            log.error("Error al enviar mensaje de contacto", e);
            redirectAttributes.addFlashAttribute("error", "Hubo un error al enviar el mensaje.");
        }

        return "redirect:/contacto";
    }
}