package com.cei.informes.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Laboratorios")
public class Laboratorio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_laboratorio")
    private Integer idLaboratorio;

    @Column(name = "nombre_actual", nullable = false, unique = true)
    private String nombreActual;

    @Column(name = "acronimo_actual", unique = true)
    private String acronimoActual;

    @Column(name = "numero_oficial_cei", nullable = false, unique = true)
    private int numeroOficialCEI;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    // Getters y Setters
    public Integer getIdLaboratorio() { return idLaboratorio; }
    public void setIdLaboratorio(Integer idLaboratorio) { this.idLaboratorio = idLaboratorio; }
    public String getNombreActual() { return nombreActual; }
    public void setNombreActual(String nombreActual) { this.nombreActual = nombreActual; }
    public String getAcronimoActual() { return acronimoActual; }
    public void setAcronimoActual(String acronimoActual) { this.acronimoActual = acronimoActual; }
    public int getNumeroOficialCEI() { return numeroOficialCEI; }
    public void setNumeroOficialCEI(int numeroOficialCEI) { this.numeroOficialCEI = numeroOficialCEI; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}