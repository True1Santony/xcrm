package com.xcrm.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class EmailSender {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCorreo(String nombre, String email, String asunto, String mensaje, MultipartFile archivo)
            throws MailException, MessagingException, IOException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        boolean tieneAdjunto = archivo != null && !archivo.isEmpty();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, tieneAdjunto, "UTF-8");

        helper.setTo("destinatario@dominio.com");  // Cambia al correo que quieras recibir los formularios
        helper.setSubject("Nuevo mensaje de contacto: " + asunto);

        // Crear HTML del correo
        String contenidoHtml = "<!DOCTYPE html>"
                + "<html><head><meta charset='UTF-8'>"
                + "<style>"
                + "  body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f9f9f9; margin: 0; padding: 0; }"
                + "  .email-container { max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); padding: 30px; }"
                + "  h2 { color: #4a90e2; margin-top: 0; }"
                + "  table { width: 100%; border-collapse: collapse; }"
                + "  td { padding: 12px 10px; vertical-align: top; }"
                + "  tr:nth-child(even) { background-color: #f2f2f2; }"
                + "  .footer { font-size: 0.9em; color: #888; text-align: center; margin-top: 30px; }"
                + "  a { color: #4a90e2; text-decoration: none; }"
                + "</style></head>"
                + "<body><div class='email-container'>"
                + "<h2>📬 Nuevo mensaje de contacto</h2>"
                + "<table>"
                + "  <tr><td><strong>👤 Nombre:</strong></td><td>" + escapeHtml(nombre) + "</td></tr>"
                + "  <tr><td><strong>📧 Email:</strong></td><td>" + escapeHtml(email) + "</td></tr>"
                + "  <tr><td><strong>📝 Asunto:</strong></td><td>" + escapeHtml(asunto) + "</td></tr>"
                + "  <tr><td><strong>💬 Mensaje:</strong></td><td>" + escapeHtml(mensaje).replaceAll("(\r\n|\n)", "<br>") + "</td></tr>"
                + "</table>"
                + "</div></body></html>";


        helper.setText(contenidoHtml, true); // true = el contenido es HTML

        // Adjuntar archivo si hay
        if (tieneAdjunto) {
            InputStreamSource attachment = new ByteArrayResource(archivo.getBytes());
            helper.addAttachment(archivo.getOriginalFilename(), attachment);
        }

        mailSender.send(mimeMessage);
    }

    // Método auxiliar para evitar inyección de HTML malicioso
    private String escapeHtml(String texto) {
        if (texto == null) return "";
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
