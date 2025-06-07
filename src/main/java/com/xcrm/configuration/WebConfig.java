package com.xcrm.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Configuración para servir archivos estáticos desde una carpeta específica en el servidor.
 * En este caso, permite acceder a las imágenes o archivos subidos por los usuarios.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Directorio en el servidor donde se almacenan los archivos subidos (configurado en application.properties)
    @Value("${uploads.dir}")
    private String uploadDir;

    /**
     * Configura la ruta pública "/uploads/**" para que sirva los archivos desde la carpeta física indicada.
     * Esto hace que los archivos subidos sean accesibles desde el navegador a través de esa URL.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/uploads/**") // Ruta pública para acceder a los archivos
                .addResourceLocations("file:" + uploadDir); // Ubicación física en el servidor
    }
}