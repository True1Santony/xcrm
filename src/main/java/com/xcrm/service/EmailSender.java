package com.xcrm.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class EmailSender {

    private final JavaMailSender mailSender;
    private final String destinatarioCorreo;
    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    @Value("${xcrm.mail.enabled:true}")
    private boolean mailEnabled;

    @Autowired
    public EmailSender(JavaMailSender mailSender,
                       @Value("${email.destinatario}") String destinatarioCorreo) {
        this.mailSender = mailSender;
        this.destinatarioCorreo = destinatarioCorreo;
    }

    /**
     * Método principal para enviar un correo con posible archivo adjunto.
     * - Valida tamaño y tipo del archivo adjunto si existe.
     * - Construye un mensaje HTML con los datos recibidos.
     * - Adjunta archivo local si se proporciona.
     * - Envía el correo al destinatario configurado.
     */
    public void enviarCorreo(String nombre, String email, String asunto, String mensaje,
                             MultipartFile archivo, String urlArchivoDropbox)
            throws MailException, MessagingException, IOException {


        if (!mailEnabled) {
            logger.info("🚫 Envío de correos deshabilitado por configuración (xcrm.mail.enabled=false)");
            return;
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        boolean tieneAdjunto = archivo != null && !archivo.isEmpty();

        if (tieneAdjunto) {
            // Validación del tamaño máximo permitido (4MB)
            long maxSize = 4 * 1024 * 1024;
            if (archivo.getSize() > maxSize) {
                throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido (4MB).");
            }

            // Validación de tipos MIME permitidos
            String tipo = archivo.getContentType();
            if (!List.of("application/pdf", "image/jpeg", "image/png",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            ).contains(tipo)) {
                throw new IllegalArgumentException("Tipo de archivo no permitido: " + tipo);
            }
        }

        // Preparación del mensaje con posibilidad de adjunto y codificación UTF-8
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, tieneAdjunto, "UTF-8");

        helper.setTo(destinatarioCorreo);
        helper.setSubject("Nuevo mensaje de contacto: " + asunto);

        // Construcción del contenido HTML del email con formato y estilos
        StringBuilder contenidoHtml = new StringBuilder();
        contenidoHtml.append("<!DOCTYPE html>")
                .append("<html><head><meta charset='UTF-8'>")
                .append("<style>")
                .append("  body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f9f9f9; margin: 0; padding: 0; }")
                .append("  .email-container { max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); padding: 30px; }")
                .append("  h2 { color: #4a90e2; margin-top: 0; }")
                .append("  table { width: 100%; border-collapse: collapse; }")
                .append("  td { padding: 12px 10px; vertical-align: top; }")
                .append("  tr:nth-child(even) { background-color: #f2f2f2; }")
                .append("  .footer { font-size: 0.9em; color: #888; text-align: center; margin-top: 30px; }")
                .append("  a { color: #4a90e2; text-decoration: none; }")
                .append("</style></head>")
                .append("<body><div class='email-container'>")
                .append("<h2>📬 Nuevo mensaje de contacto</h2>")
                .append("<table>")
                .append("  <tr><td><strong>👤 Nombre:</strong></td><td>").append(escapeHtml(nombre)).append("</td></tr>")
                .append("  <tr><td><strong>📧 Email:</strong></td><td>").append(escapeHtml(email)).append("</td></tr>")
                .append("  <tr><td><strong>📝 Asunto:</strong></td><td>").append(escapeHtml(asunto)).append("</td></tr>")
                .append("  <tr><td><strong>💬 Mensaje:</strong></td><td>").append(escapeHtml(mensaje).replaceAll("(\r\n|\n)", "<br>")).append("</td></tr>");

        // Añade enlace a archivo en Dropbox si se proporcionó
        if (urlArchivoDropbox != null && !urlArchivoDropbox.isEmpty()) {
            contenidoHtml.append("<tr><td><strong>📂 Archivo desde Dropbox:</strong></td><td><a href='")
                    .append(urlArchivoDropbox)
                    .append("'>Ver archivo</a></td></tr>");
        }

        contenidoHtml.append("</table></div></body></html>");

        helper.setText(contenidoHtml.toString(), true);

        // Adjunta archivo local al correo si existe
        if (tieneAdjunto) {
            InputStreamSource attachment = new ByteArrayResource(archivo.getBytes());
            helper.addAttachment(archivo.getOriginalFilename(), attachment);
        }

        // Envía el correo configurado
        mailSender.send(mimeMessage);
    }

    /**
     * Método auxiliar para escapar caracteres HTML que podrían causar problemas de seguridad o visualización.
     */
    private String escapeHtml(String texto) {
        if (texto == null) return "";
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}