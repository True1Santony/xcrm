package com.xcrm.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class ImageService {

    @Value("${uploads.dir}") // Asegúrate que termina en "/" en application.properties
    private String uploadDir;

    /**
     * Guarda la imagen en el sistema de archivos y devuelve la ruta pública accesible.
     *
     * @param file     imagen a guardar
     * @param filename nombre del archivo (ej: uuid.jpg)
     * @return ruta accesible por navegador (ej: "/uploads/uuid.jpg")
     * @throws IllegalArgumentException si el archivo no es una imagen válida
     * @throws IOException si ocurre un error al guardar el archivo
     */
    public String guardarImagen(MultipartFile file, String filename) throws IOException {
        // Validar tipo MIME
        if (!file.getContentType().startsWith("image/")) {
            System.out.println("Archivo rechazado: tipo no permitido: " + file.getContentType());
            throw new IllegalArgumentException("Solo se permiten archivos de imagen.");
        }

        // Validar tamaño
        if (file.getSize() > 2 * 1024 * 1024) {
            System.out.println("Archivo rechazado: tamaño excedido (" + file.getSize() + ")");
            throw new IllegalArgumentException("La imagen debe pesar menos de 2MB.");
        }

        // Crear archivo de destino
        File destino = new File(uploadDir, filename);
        destino.getParentFile().mkdirs(); // Asegura que los directorios existan

        // Mostrar ruta absoluta para verificación
        System.out.println("Guardando imagen en: " + destino.getAbsolutePath());

        // Transferir el archivo
        System.out.println("== GUARDANDO EN: " + destino.getAbsolutePath());
        file.transferTo(destino);

        // Devolver ruta accesible por navegador
        return "/uploads/" + filename;
    }
}