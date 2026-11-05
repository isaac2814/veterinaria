package com.apiveterinaria.veterinaria.repositories;

import com.apiveterinaria.veterinaria.enums.TipoMovimiento;
import com.apiveterinaria.veterinaria.models.MovimientoInventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Repository
public interface MovimientoInventarioRepository extends Repository<MovimientoInventario, UUID> {

    MovimientoInventario save(MovimientoInventario entity);

    <S extends MovimientoInventario> S saveAndFlush(S entity);

    Optional<MovimientoInventario> findById(UUID id);

    boolean existsById(UUID id);

    boolean existsByIdOperacion(String idOperacion);

    @EntityGraph(attributePaths = {"lote", "lote.vacuna", "usuario"})
    @Query("SELECT m FROM MovimientoInventario m WHERE m.id = :id")
    Optional<MovimientoInventario> findByIdWithRelaciones(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"lote", "lote.vacuna", "usuario"})
    @Query("""
        SELECT m FROM MovimientoInventario m
        WHERE (:loteId IS NULL OR m.lote.id = :loteId)
          AND (:tipo IS NULL OR m.tipoMovimiento = :tipo)
          AND (:desde IS NULL OR m.fechaMovimiento >= :desde)
          AND (:hasta IS NULL OR m.fechaMovimiento <= :hasta)
    """)
    Page<MovimientoInventario> findConFiltros(
            @Param("loteId") UUID loteId,
            @Param("tipo") TipoMovimiento tipo,
            @Param("desde") OffsetDateTime desde,
            @Param("hasta") OffsetDateTime hasta,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"lote", "lote.vacuna", "usuario"})
    @Query("SELECT m FROM MovimientoInventario m WHERE m.lote.id = :loteId")
    Page<MovimientoInventario> findByLoteId(@Param("loteId") UUID loteId, Pageable pageable);
}
