package com.cei.informes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Lee la ruta de la carpeta de uploads desde application.properties
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Convierte la ruta de la carpeta (ej. C:/cei_informes_uploads) a una ruta de recurso
        Path uploadPath = Paths.get(uploadDir);
        String uploadPathUrl = uploadPath.toAbsolutePath().normalize().toString();

        // Mapea la URL /files/** a la carpeta física de uploads
        // La parte "file:/" es crucial para que Spring sepa que es una ruta del sistema de archivos
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:/" + uploadPathUrl + "/");
    }
}