package com.cei.informes.controller;

import com.cei.informes.model.InformeTecnico;
import com.cei.informes.model.Laboratorio;
import com.cei.informes.repository.InformeTecnicoRepository;
import com.cei.informes.repository.LaboratorioRepository;
import com.cei.informes.service.FileStorageService;
import com.cei.informes.service.InformeSpecification;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class InformeController {

    @Autowired
    private InformeTecnicoRepository informeRepository;
    @Autowired
    private LaboratorioRepository laboratorioRepository;
    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/laboratorios")
    public List<Laboratorio> getActiveLaboratorios() {
        return laboratorioRepository.findByActivoTrueOrderByNombreActualAsc();
    }

    @PostMapping(value = "/informes", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<InformeTecnico> createInforme(@ModelAttribute InformeTecnico informe, @RequestParam("archivo") MultipartFile archivo) {
        Laboratorio lab = laboratorioRepository.findById(informe.getLaboratorio().getIdLaboratorio())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Laboratorio no encontrado con ID: " + informe.getLaboratorio().getIdLaboratorio()));
        informe.setLaboratorio(lab);
        
        // Formato de ID actualizado para incluir el número de solicitud
        String idCEI = String.format("CEI-%02d-%s-%s-%d",
                lab.getNumeroOficialCEI(),
                informe.getNumeroSolicitud(),
                String.format("%03d", informe.getNumeroSecuencialInforme()),
                informe.getAnioInforme());
        informe.setIdInformeCEI(idCEI);

        // Llamada actualizada al servicio de archivos
        String relativePath = fileStorageService.storeFile(archivo, informe);
        informe.setRutaArchivoDigitalizado(relativePath);

        InformeTecnico informeGuardado = informeRepository.save(informe);
        return ResponseEntity.ok(informeGuardado);
    }
    
    @GetMapping("/informes")
    public List<InformeTecnico> searchInformes(
            @RequestParam(required = false) Integer idLaboratorioFk,
            @RequestParam(required = false) Integer anioInforme,
            @RequestParam(required = false) String numeroSolicitud, // Búsqueda por solicitud añadida
            @RequestParam(required = false) String responsableTecnico,
            @RequestParam(required = false) String empresaCliente) {
        
        Specification<InformeTecnico> spec = InformeSpecification.findByCriteria(idLaboratorioFk, anioInforme, numeroSolicitud, responsableTecnico, empresaCliente);
        return informeRepository.findAll(spec);
    }

    @GetMapping("/informes/{id}")
    public ResponseEntity<InformeTecnico> getInformeById(@PathVariable Integer id) {
        InformeTecnico informe = informeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Informe no encontrado con ID: " + id));
        return ResponseEntity.ok(informe);
    }
    
    @PutMapping(value = "/informes/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<InformeTecnico> updateInforme(@PathVariable Integer id, @ModelAttribute InformeTecnico informeDetails, @RequestParam(required = false) MultipartFile archivo) {
        InformeTecnico informeExistente = informeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Informe no encontrado con ID: " + id));

        Laboratorio lab = laboratorioRepository.findById(informeDetails.getLaboratorio().getIdLaboratorio())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Laboratorio no encontrado con ID: " + informeDetails.getLaboratorio().getIdLaboratorio()));
        informeExistente.setLaboratorio(lab);
        
        // Se añade el nuevo campo al actualizar
        informeExistente.setNumeroSolicitud(informeDetails.getNumeroSolicitud());
        informeExistente.setNumeroSecuencialInforme(informeDetails.getNumeroSecuencialInforme());
        informeExistente.setAnioInforme(informeDetails.getAnioInforme());
        informeExistente.setTipoSolicitud(informeDetails.getTipoSolicitud());
        informeExistente.setIdInformeOriginal(informeDetails.getIdInformeOriginal());
        informeExistente.setDescripcionInforme(informeDetails.getDescripcionInforme());
        informeExistente.setEmpresaCliente(informeDetails.getEmpresaCliente());
        informeExistente.setNumeroPaginas(informeDetails.getNumeroPaginas());
        informeExistente.setResponsableTecnico(informeDetails.getResponsableTecnico());
        
        // Se recalcula el ID con el nuevo formato
        String idCEI = String.format("CEI-%02d-%s-%s-%d",
                lab.getNumeroOficialCEI(),
                informeExistente.getNumeroSolicitud(),
                String.format("%03d", informeExistente.getNumeroSecuencialInforme()),
                informeExistente.getAnioInforme());
        informeExistente.setIdInformeCEI(idCEI);
        
        if (archivo != null && !archivo.isEmpty()) {
            String newRelativePath = fileStorageService.storeFile(archivo, informeExistente);
            informeExistente.setRutaArchivoDigitalizado(newRelativePath);
        }

        InformeTecnico informeActualizado = informeRepository.save(informeExistente);
        return ResponseEntity.ok(informeActualizado);
    }

    // Método de descarga de archivos actualizado y más robusto
    @GetMapping("/files/**")
    public ResponseEntity<Resource> downloadFile(HttpServletRequest request) {
        String fullPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String apiPrefix = "/api/files/";
        String relativePath = fullPath.substring(apiPrefix.length());

        Resource resource = fileStorageService.loadFileAsResource(relativePath);
        
        String contentType = "application/octet-stream";
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            if(contentType == null) {
                contentType = "application/octet-stream";
            }
        } catch (Exception e) {
            // Ignorar
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}