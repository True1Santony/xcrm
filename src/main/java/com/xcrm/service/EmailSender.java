package com.xcrm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.contacto.destinatario}")
    private String destinatario;

    public void enviarCorreo(String nombre, String email, String asunto, String mensajeTexto) {
        System.out.println("Preparando correo...");

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(email);  // O puedes usar el correo de Mailtrap: setFrom("99d0c08f662d48@smtp.mailtrap.io")
        mensaje.setTo(destinatario);
        mensaje.setSubject("Nuevo mensaje de contacto: " + asunto);
        mensaje.setText("Nombre: " + nombre + "\n"
                + "Correo: " + email + "\n"
                + "Asunto: " + asunto + "\n"
                + "Mensaje:\n" + mensajeTexto);

        try {
            mailSender.send(mensaje);
            System.out.println("Correo enviado correctamente.");
        } catch (Exception e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
