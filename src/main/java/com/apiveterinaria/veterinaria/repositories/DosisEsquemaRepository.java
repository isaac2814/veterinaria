package com.apiveterinaria.veterinaria.repositories;

import com.apiveterinaria.veterinaria.models.DosisEsquema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DosisEsquemaRepository extends JpaRepository<DosisEsquema, UUID> {

    List<DosisEsquema> findByEsquemaIdOrderByNumeroDosisAsc(UUID esquemaId);

    Optional<DosisEsquema> findByEsquemaIdAndNumeroDosis(UUID esquemaId, Integer numeroDosis);

    boolean existsByEsquemaIdAndNumeroDosis(UUID esquemaId, Integer numeroDosis);
}
