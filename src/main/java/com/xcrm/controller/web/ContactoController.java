package com.xcrm.controller.web;

import com.xcrm.model.ContactoMensaje;
import com.xcrm.model.User;

import com.xcrm.service.ContactoMensajeService;
import com.xcrm.service.EmailSender;
import com.xcrm.service.UserService;
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
    public String procesarFormularioContacto(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String asunto,
            @RequestParam String mensaje,
            @RequestPart(required = false) MultipartFile archivo,
            @RequestParam(required = false) String archivoDropbox,
            RedirectAttributes redirectAttributes) {

        // Registra en el log qué archivos se han adjuntado (local o Dropbox)
        String infoAdjuntos = "";
        if (archivo != null && !archivo.isEmpty()) {
            infoAdjuntos += String.format("📎 archivo local: '%s' (%d KB)", archivo.getOriginalFilename(), archivo.getSize() / 1024);
        } else {
            infoAdjuntos += "📎 archivo local: no adjunto";
        }

        if (archivoDropbox != null && !archivoDropbox.trim().isEmpty()) {
            infoAdjuntos += String.format(", 🌐 Dropbox: %s", archivoDropbox);
        } else {
            infoAdjuntos += ", 🌐 Dropbox: no adjunto";
        }

        // Imprime en log todos los datos del formulario para facilitar trazabilidad
        log.info("Formulario recibido: nombre='{}', email='{}', asunto='{}', mensaje='{}', {}",
                nombre, email, asunto, mensaje, infoAdjuntos);

        try {
            // Envía un correo con la información del formulario y los archivos (si los hay)
            emailSender.enviarCorreo(nombre, email, asunto, mensaje, archivo, archivoDropbox);

            // Crea un objeto con todos los datos del mensaje para guardarlo
            ContactoMensaje mensajeContacto = new ContactoMensaje();
            mensajeContacto.setNombre(nombre);
            mensajeContacto.setEmail(email);
            mensajeContacto.setAsunto(asunto);
            mensajeContacto.setMensaje(mensaje);
            mensajeContacto.setArchivoDropboxUrl(archivoDropbox);

            // Si hay archivo adjunto, guarda su nombre
            if (archivo != null && !archivo.isEmpty()) {
                mensajeContacto.setNombreArchivoAdjunto(archivo.getOriginalFilename());
            }

            // Asocia el mensaje al usuario autenticado o le genera un ID anónimo
            String usuarioId = obtenerIdUsuarioAutenticado();
            mensajeContacto.setUsuarioId(usuarioId);

            // Guarda el mensaje en la base de datos
            contactoMensajeService.guardarMensajeContacto(mensajeContacto);

            // Muestra un mensaje de éxito al usuario
            redirectAttributes.addFlashAttribute("success", "¡Mensaje enviado correctamente!");

        } catch (IllegalArgumentException e) {
            // Manejo de errores de validación
            log.warn("Validación fallida: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            // Manejo de cualquier otro error inesperado
            log.error("Error al enviar mensaje de contacto", e);
            redirectAttributes.addFlashAttribute("error", "Hubo un error al enviar el mensaje.");
        }

        // Redirige al formulario después de enviar el mensaje
        return "redirect:/contacto";
    }

    /**
     * Devuelve el ID del usuario actual si está autenticado.
     * Si no hay sesión iniciada, genera un UUID aleatorio como identificador anónimo.
     */
    private String obtenerIdUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName();
            User user = userService.findByUsername(username);
            if (user != null) {
                return user.getId().toString();
            }
        }
        return UUID.randomUUID().toString(); // ID para usuarios no logueados
    }
}