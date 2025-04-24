package com.xcrm.controller.web;

import com.xcrm.service.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Arrays;
import java.util.List;

@Controller
public class ContactoController {

    private static final Logger logger = LoggerFactory.getLogger(ContactoController.class);

    private final EmailSender emailSender;

    @Autowired
    public ContactoController(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @PostMapping("/enviar-contacto")
    public String enviarContacto(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String asunto,
            @RequestParam String mensaje,
            @RequestPart(required = false) MultipartFile archivo, // Recibe el archivo adjunto
            RedirectAttributes redirectAttributes) {

        String archivoAdjunto = (archivo != null && !archivo.isEmpty()) ? "con archivo adjunto" : "sin archivo adjunto";
        logger.info("Formulario recibido: nombre={}, email={}, asunto={}, mensaje={}, archivo: {}",
                nombre, email, asunto, mensaje, archivoAdjunto);

        try {
            // Llamar al servicio de envío de correo, pasando el archivo adjunto
            emailSender.enviarCorreo(nombre, email, asunto, mensaje, archivo);
            redirectAttributes.addFlashAttribute("success", "¡Mensaje enviado correctamente!");
        } catch (Exception e) {
            logger.error("Error al enviar mensaje de contacto", e);
            redirectAttributes.addFlashAttribute("error", "Hubo un error al enviar el mensaje.");
        }

        return "redirect:/contacto";
    }
}