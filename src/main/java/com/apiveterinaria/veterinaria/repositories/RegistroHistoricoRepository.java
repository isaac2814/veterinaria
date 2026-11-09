package com.apiveterinaria.veterinaria.repositories;

import com.apiveterinaria.veterinaria.models.RegistroHistorico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistroHistoricoRepository extends JpaRepository<RegistroHistorico, UUID> {

    @EntityGraph(attributePaths = {"usuarioActor"})
    @Query("""
        SELECT h FROM RegistroHistorico h
        WHERE (:entidad IS NULL OR LOWER(h.entidad) = :entidad)
          AND (:entidadId IS NULL OR h.entidadId = :entidadId)
          AND (:accion IS NULL OR LOWER(h.accion) = :accion)
          AND (:desde IS NULL OR h.fechaEvento >= :desde)
          AND (:hasta IS NULL OR h.fechaEvento <= :hasta)
    """)
    Page<RegistroHistorico> findConFiltros(
            @Param("entidad") String entidad,
            @Param("entidadId") UUID entidadId,
            @Param("accion") String accion,
            @Param("desde") OffsetDateTime desde,
            @Param("hasta") OffsetDateTime hasta,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"usuarioActor"})
    @Query("SELECT h FROM RegistroHistorico h WHERE h.id = :id")
    Optional<RegistroHistorico> findByIdWithRelaciones(@Param("id") UUID id);
}
