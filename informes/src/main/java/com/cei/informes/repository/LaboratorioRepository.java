package com.cei.informes.repository;

import com.cei.informes.model.Laboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Integer> {
    List<Laboratorio> findByActivoTrueOrderByNombreActualAsc();
}