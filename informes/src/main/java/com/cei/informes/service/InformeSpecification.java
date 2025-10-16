package com.cei.informes.service;

import com.cei.informes.model.InformeTecnico;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.List;

public class InformeSpecification {

    public static Specification<InformeTecnico> findByCriteria(
        Integer idLaboratorioFk, 
        Integer anioInforme, 
        String numeroSolicitud, // --- Añadido parámetro ---
        String responsableTecnico, 
        String empresaCliente) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (idLaboratorioFk != null && idLaboratorioFk > 0) {
                predicates.add(criteriaBuilder.equal(root.get("laboratorio").get("idLaboratorio"), idLaboratorioFk));
            }
            if (anioInforme != null && anioInforme > 0) {
                predicates.add(criteriaBuilder.equal(root.get("anioInforme"), anioInforme));
            }
            // --- Añadida lógica para buscar por número de solicitud ---
            if (StringUtils.hasText(numeroSolicitud)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("numeroSolicitud")), "%" + numeroSolicitud.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(responsableTecnico)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("responsableTecnico")), "%" + responsableTecnico.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(empresaCliente)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("empresaCliente")), "%" + empresaCliente.toLowerCase() + "%"));
            }

            // Ordenar los resultados por año y luego por número de informe, de forma descendente
            query.orderBy(criteriaBuilder.desc(root.get("anioInforme")), criteriaBuilder.desc(root.get("numeroSecuencialInforme")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}