package com.apiveterinaria.veterinaria.repositories;

import com.apiveterinaria.veterinaria.models.LoteVacuna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoteVacunaRepository extends JpaRepository<LoteVacuna, UUID> {

    boolean existsByVacunaIdAndNumeroLote(UUID vacunaId, String numeroLote);

    boolean existsByVacunaIdAndNumeroLoteAndIdNot(UUID vacunaId, String numeroLote, UUID id);

    @EntityGraph(attributePaths = {"vacuna", "registradoPor"})
    @Query("SELECT l FROM LoteVacuna l WHERE l.id = :id")
    Optional<LoteVacuna> findByIdWithRelaciones(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"vacuna", "registradoPor"})
    @Query("""
        SELECT l FROM LoteVacuna l
        WHERE (:vacunaId IS NULL OR l.vacuna.id = :vacunaId)
          AND (:soloConStock IS NULL OR :soloConStock = false OR l.cantidadDisponible > 0)
          AND (:vencidos IS NULL OR (:vencidos = true AND l.fechaVencimiento < :hoy) OR (:vencidos = false AND l.fechaVencimiento >= :hoy))
    """)
    Page<LoteVacuna> findConFiltros(
            @Param("vacunaId") UUID vacunaId,
            @Param("soloConStock") Boolean soloConStock,
            @Param("vencidos") Boolean vencidos,
            @Param("hoy") LocalDate hoy,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"vacuna", "registradoPor"})
    @Query("""
        SELECT l FROM LoteVacuna l
        WHERE l.cantidadDisponible > 0
          AND l.fechaVencimiento >= :hoy
          AND l.fechaVencimiento <= :fechaLimite
        ORDER BY l.fechaVencimiento ASC
    """)
    List<LoteVacuna> findPorVencer(
            @Param("hoy") LocalDate hoy,
            @Param("fechaLimite") LocalDate fechaLimite
    );
}
