package com.cei.informes.repository;

import com.cei.informes.model.InformeTecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InformeTecnicoRepository extends JpaRepository<InformeTecnico, Integer>, JpaSpecificationExecutor<InformeTecnico> {
    // JpaSpecificationExecutor nos permitirá construir las búsquedas dinámicas complejas
}