package com.xcrm.service;



import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.MailException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

@Service
public class EmailSender {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCorreo(String nombre, String email, String asunto, String mensaje, MultipartFile archivo) throws MailException, MessagingException, IOException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo("destinatario@dominio.com");  // Dirección del destinatario
        helper.setSubject(asunto);
        helper.setText("Nombre: " + nombre + "\nEmail: " + email + "\nMensaje: " + mensaje);

        // Adjuntar archivo si se ha subido
        if (archivo != null && !archivo.isEmpty()) {
            // Guardar el archivo temporalmente y adjuntarlo
            String archivoNombre = archivo.getOriginalFilename();
            helper.addAttachment(archivoNombre, archivo);
        }

        // Enviar correo
        mailSender.send(mimeMessage);
    }
}

