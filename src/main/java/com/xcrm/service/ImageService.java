package com.xcrm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    @Value("${uploads.dir}")
    private String uploadsDirectory;

    /**
     * Guarda una imagen en el sistema de archivos y devuelve la ruta pública para acceder a ella.
     *
     * @param imageFile archivo de imagen a guardar
     * @param filename  nombre con el que se guardará el archivo (ejemplo: uuid.jpg)
     * @return ruta pública accesible desde el navegador (ejemplo: "/uploads/uuid.jpg")
     * @throws IllegalArgumentException si el archivo no es una imagen válida o supera el tamaño permitido
     * @throws IOException si ocurre un error al guardar el archivo
     */
    public String saveImage(MultipartFile imageFile, String filename) throws IOException {
        // Validar que sea un archivo de imagen
        if (!imageFile.getContentType().startsWith("image/")) {
            logger.warn("Archivo rechazado: tipo no permitido: {}", imageFile.getContentType());
            throw new IllegalArgumentException("Solo se permiten archivos de imagen.");
        }

        // Validar tamaño máximo permitido (2MB)
        if (imageFile.getSize() > 2 * 1024 * 1024) {
            logger.warn("Archivo rechazado: tamaño excedido ({} bytes)", imageFile.getSize());
            throw new IllegalArgumentException("La imagen debe pesar menos de 2MB.");
        }

        File uploadDirFile = new File(uploadsDirectory);
        if (!uploadDirFile.exists()) {
            boolean created = uploadDirFile.mkdirs();
            if (!created) {
                logger.error("No se pudo crear el directorio: {}", uploadDirFile.getAbsolutePath());
                throw new IOException("No se pudo crear el directorio: " + uploadDirFile.getAbsolutePath());
            }
        }

        File destinationFile = new File(uploadsDirectory, filename);

        logger.info("Guardando imagen en: {}", destinationFile.getAbsolutePath());

        imageFile.transferTo(destinationFile);

        return "/uploads/" + filename;
    }
}