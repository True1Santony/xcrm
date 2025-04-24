package com.xcrm.controller.web;

import com.xcrm.service.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactoController {

    private static final Logger logger = LoggerFactory.getLogger(ContactoController.class);

    private final EmailSender emailSender;

    @Value("${dropbox.app.key}")
    private String dropboxAppKey;

    @Autowired
    public ContactoController(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

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

        if (archivo != null && !archivo.isEmpty()) {
            logArchivo += String.format("📎 archivo local: '%s' (%d KB)", archivo.getOriginalFilename(), archivo.getSize() / 1024);
        } else {
            logArchivo += "📎 archivo local: no adjunto";
        }

        if (archivoDropbox != null && !archivoDropbox.trim().isEmpty()) {
            logArchivo += String.format(", 🌐 Dropbox: %s", archivoDropbox);
        } else {
            logArchivo += ", 🌐 Dropbox: no adjunto";
        }

        logger.info("Formulario recibido: nombre='{}', email='{}', asunto='{}', mensaje='{}', {}",
                nombre, email, asunto, mensaje, logArchivo);

        try {
            emailSender.enviarCorreo(nombre, email, asunto, mensaje, archivo, archivoDropbox);
            redirectAttributes.addFlashAttribute("success", "¡Mensaje enviado correctamente!");
        } catch (IllegalArgumentException e) {
            logger.warn("Validación fallida: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("Error al enviar mensaje de contacto", e);
            redirectAttributes.addFlashAttribute("error", "Hubo un error al enviar el mensaje.");
        }

        return "redirect:/contacto";
    }
}