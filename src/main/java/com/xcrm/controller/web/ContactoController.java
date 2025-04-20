package com.xcrm.controller.web;

import com.xcrm.service.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactoController {

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

        System.out.println("Formulario recibido: nombre=" + nombre + ", email=" + email + ", asunto=" + asunto + ", mensaje=" + mensaje);

        try {
            // Llamar al servicio de envío de correo, pasando el archivo adjunto
            emailSender.enviarCorreo(nombre, email, asunto, mensaje, archivo);
            redirectAttributes.addFlashAttribute("success", "¡Mensaje enviado correctamente!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Hubo un error al enviar el mensaje.");
        }

        return "redirect:/contacto";
    }
}
