package com.apiveterinaria.veterinaria.repositories;

import com.apiveterinaria.veterinaria.models.EsquemaVacunacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EsquemaVacunacionRepository extends JpaRepository<EsquemaVacunacion, UUID> {

    @Query(value = "SELECT e FROM EsquemaVacunacion e " +
            "JOIN FETCH e.vacuna v " +
            "JOIN FETCH e.especie esp " +
            "WHERE (:vacunaId IS NULL OR v.id = :vacunaId) " +
            "  AND (:especieId IS NULL OR esp.id = :especieId)",
            countQuery = "SELECT COUNT(e) FROM EsquemaVacunacion e " +
            "WHERE (:vacunaId IS NULL OR e.vacuna.id = :vacunaId) " +
            "  AND (:especieId IS NULL OR e.especie.id = :especieId)")
    Page<EsquemaVacunacion> buscarEsquemas(@Param("vacunaId") UUID vacunaId,
                                           @Param("especieId") UUID especieId,
                                           Pageable pageable);

    @Query("SELECT e FROM EsquemaVacunacion e " +
            "JOIN FETCH e.vacuna v " +
            "JOIN FETCH e.especie esp " +
            "WHERE e.id = :id")
    Optional<EsquemaVacunacion> findWithDetallesById(@Param("id") UUID id);

    boolean existsByVacunaId(UUID vacunaId);

    boolean existsByVacunaIdAndEspecieIdAndNombreIgnoreCaseAndVersion(UUID vacunaId, UUID especieId, String nombre, Integer version);
}
