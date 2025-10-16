package com.cei.informes.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "Informes_Tecnicos", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_laboratorio_fk", "numero_secuencial_informe", "anio_informe"})
})
public class InformeTecnico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_informe")
    private Integer idInforme;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_laboratorio_fk", nullable = false)
    private Laboratorio laboratorio;

    @Column(name = "numero_solicitud", nullable = false, length = 50)
    private String numeroSolicitud;

    @Column(name = "numero_secuencial_informe", nullable = false)
    private Integer numeroSecuencialInforme;

    @Column(name = "anio_informe", nullable = false)
    private Integer anioInforme;
    
    @Column(name = "tipo_solicitud", nullable = false, length = 2)
    private String tipoSolicitud;

    @Column(name = "id_informe_cei", unique = true)
    private String idInformeCEI;

    @Column(name = "id_informe_original", length = 255)
    private String idInformeOriginal;

    @Column(name = "descripcion_informe", columnDefinition = "TEXT")
    private String descripcionInforme;

    @Column(name = "empresa_cliente", length = 255)
    private String empresaCliente;

    @Column(name = "numero_paginas")
    private Integer numeroPaginas;

    @Column(name = "responsable_tecnico", length = 255)
    private String responsableTecnico;

    @Column(name = "ruta_archivo_digitalizado", length = 1024)
    private String rutaArchivoDigitalizado;

    @Column(name = "fecha_registro_sistema", nullable = false, updatable = false)
    private LocalDateTime fechaRegistroSistema;
    
    @Column(name = "observaciones_formato", columnDefinition = "TEXT")
    private String observacionesFormato;

    @PrePersist
    protected void onCreate() {
        this.fechaRegistroSistema = LocalDateTime.now();
    }

    // Getters y Setters (Asegúrate de que estén todos)
    public Integer getIdInforme() { return idInforme; }
    public void setIdInforme(Integer idInforme) { this.idInforme = idInforme; }
    public Laboratorio getLaboratorio() { return laboratorio; }
    public void setLaboratorio(Laboratorio laboratorio) { this.laboratorio = laboratorio; }
    public Integer getNumeroSecuencialInforme() { return numeroSecuencialInforme; }
    public void setNumeroSecuencialInforme(Integer numeroSecuencialInforme) { this.numeroSecuencialInforme = numeroSecuencialInforme; }
    public Integer getAnioInforme() { return anioInforme; }
    public void setAnioInforme(Integer anioInforme) { this.anioInforme = anioInforme; }
    public String getTipoSolicitud() { return tipoSolicitud; }
    public void setTipoSolicitud(String tipoSolicitud) { this.tipoSolicitud = tipoSolicitud; }
    public String getIdInformeCEI() { return idInformeCEI; }
    public void setIdInformeCEI(String idInformeCEI) { this.idInformeCEI = idInformeCEI; }
    public String getIdInformeOriginal() { return idInformeOriginal; }
    public void setIdInformeOriginal(String idInformeOriginal) { this.idInformeOriginal = idInformeOriginal; }
    public String getDescripcionInforme() { return descripcionInforme; }
    public void setDescripcionInforme(String descripcionInforme) { this.descripcionInforme = descripcionInforme; }
    public String getEmpresaCliente() { return empresaCliente; }
    public void setEmpresaCliente(String empresaCliente) { this.empresaCliente = empresaCliente; }
    public Integer getNumeroPaginas() { return numeroPaginas; }
    public void setNumeroPaginas(Integer numeroPaginas) { this.numeroPaginas = numeroPaginas; }
    public String getResponsableTecnico() { return responsableTecnico; }
    public void setResponsableTecnico(String responsableTecnico) { this.responsableTecnico = responsableTecnico; }
    public String getRutaArchivoDigitalizado() { return rutaArchivoDigitalizado; }
    public void setRutaArchivoDigitalizado(String rutaArchivoDigitalizado) { this.rutaArchivoDigitalizado = rutaArchivoDigitalizado; }
    public LocalDateTime getFechaRegistroSistema() { return fechaRegistroSistema; }
    public void setFechaRegistroSistema(LocalDateTime fechaRegistroSistema) { this.fechaRegistroSistema = fechaRegistroSistema; }
    public String getObservacionesFormato() { return observacionesFormato; }
    public void setObservacionesFormato(String observacionesFormato) { this.observacionesFormato = observacionesFormato; }
    public String getNumeroSolicitud() { return numeroSolicitud; }
    public void setNumeroSolicitud(String numeroSolicitud) { this.numeroSolicitud = numeroSolicitud; }
}