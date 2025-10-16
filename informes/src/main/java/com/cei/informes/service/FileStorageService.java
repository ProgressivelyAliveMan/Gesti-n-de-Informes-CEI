package com.cei.informes.service;

import com.cei.informes.model.InformeTecnico;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        // La ubicación base para todos los archivos, ej: C:/cei_informes_uploads
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio raíz para los archivos.", ex);
        }
    }

    /**
     * Guarda un archivo en una estructura de carpetas jerárquica.
     * @param file El archivo a guardar.
     * @param informe El objeto InformeTecnico con todos los datos.
     * @return La ruta relativa del archivo guardado (ej: "2025/LABGEO/INTERNA/CEI-01-S123-001-2025.pdf").
     */
    public String storeFile(MultipartFile file, InformeTecnico informe) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new RuntimeException("No se puede guardar un archivo sin nombre.");
        }

        // 1. Construir la estructura de la carpeta
        String anio = String.valueOf(informe.getAnioInforme());
        String labAcronimo = informe.getLaboratorio().getAcronimoActual();
        String tipoSolicitudDir = informe.getTipoSolicitud().equals("SI") ? "SOLICITUD_INTERNA" : "SOLICITUD_EXTERNA";

        // Ruta de la carpeta final, ej: C:/cei_informes_uploads/2025/LABGEO/SOLICITUD_INTERNA
        Path targetDirectory = this.fileStorageLocation.resolve(Paths.get(anio, labAcronimo, tipoSolicitudDir)).normalize();

        try {
            // Crear las carpetas anidadas si no existen
            Files.createDirectories(targetDirectory);
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo crear la estructura de carpetas para guardar el archivo.", ex);
        }

        // 2. Construir el nombre del archivo final (usamos el ID_Informe_CEI para unicidad)
        String extension = ".pdf"; // Asumimos PDF, puedes hacerlo más dinámico si quieres
        try {
            extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        } catch (Exception e) {
            // No hacer nada, se queda .pdf por defecto
        }
        String fileName = informe.getIdInformeCEI().replaceAll("[^a-zA-Z0-9.-]", "_") + extension;

        // 3. Guardar el archivo
        try {
            Path targetLocation = targetDirectory.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // 4. Devolver la RUTA RELATIVA para guardar en la BD
            return Paths.get(anio, labAcronimo, tipoSolicitudDir, fileName).toString().replace('\\', '/');
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo guardar el archivo " + fileName, ex);
        }
    }

    /**
     * Carga un archivo como un recurso a partir de su ruta relativa.
     * @param relativePath La ruta relativa almacenada en la base de datos.
     * @return El recurso del archivo.
     */
    public Resource loadFileAsResource(String relativePath) {
        try {
            // Construir la ruta absoluta combinando la ubicación base y la ruta relativa
            Path filePath = this.fileStorageLocation.resolve(relativePath).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("Archivo no encontrado en la ruta: " + relativePath);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Ruta de archivo inválida: " + relativePath, ex);
        }
    }
}