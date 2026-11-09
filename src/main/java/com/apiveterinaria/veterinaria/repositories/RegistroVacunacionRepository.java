package com.apiveterinaria.veterinaria.repositories;

import com.apiveterinaria.veterinaria.enums.EstadoRefuerzo;
import com.apiveterinaria.veterinaria.enums.EstadoRegistro;
import com.apiveterinaria.veterinaria.models.RegistroVacunacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistroVacunacionRepository extends JpaRepository<RegistroVacunacion, UUID> {

    boolean existsByMascotaIdAndDosisEsquemaIdAndEstadoRegistro(
            UUID mascotaId,
            UUID dosisEsquemaId,
            EstadoRegistro estadoRegistro
    );

    @Modifying
    @Query(nativeQuery = true, value = """
        UPDATE registros_vacunacion
           SET estado_registro = 'ANULADO',
               motivo_anulacion = :motivo,
               anulado_por = :usuarioId
         WHERE id = :id
           AND estado_registro = 'ACTIVO'
    """)
    int anular(
            @Param("id") UUID id,
            @Param("motivo") String motivo,
            @Param("usuarioId") UUID usuarioId
    );

    @EntityGraph(attributePaths = {
            "mascota", "mascota.propietario", "loteVacuna", "loteVacuna.vacuna",
            "veterinario", "dosisEsquema", "dosisEsquema.esquema", "anuladoPor"
    })
    @Query("SELECT r FROM RegistroVacunacion r WHERE r.id = :id")
    Optional<RegistroVacunacion> findByIdWithRelaciones(@Param("id") UUID id);

    @EntityGraph(attributePaths = {
            "mascota", "mascota.propietario", "loteVacuna", "loteVacuna.vacuna",
            "veterinario", "dosisEsquema", "dosisEsquema.esquema", "anuladoPor"
    })
    @Query("""
        SELECT r FROM RegistroVacunacion r
        WHERE (CAST(:mascotaId AS uuid) IS NULL OR r.mascota.id = :mascotaId)
          AND (CAST(:vacunaId AS uuid) IS NULL OR r.loteVacuna.vacuna.id = :vacunaId)
          AND (CAST(:veterinarioId AS uuid) IS NULL OR r.veterinario.id = :veterinarioId)
          AND (:estado IS NULL OR r.estadoRegistro = :estado)
          AND (:estadoRefuerzo IS NULL OR r.estadoRefuerzo = :estadoRefuerzo)
          AND (CAST(:desde AS date) IS NULL OR r.fechaAplicacion >= :desde)
          AND (CAST(:hasta AS date) IS NULL OR r.fechaAplicacion <= :hasta)
    """)
    Page<RegistroVacunacion> findConFiltros(
            @Param("mascotaId") UUID mascotaId,
            @Param("vacunaId") UUID vacunaId,
            @Param("veterinarioId") UUID veterinarioId,
            @Param("estado") EstadoRegistro estado,
            @Param("estadoRefuerzo") EstadoRefuerzo estadoRefuerzo,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "mascota", "mascota.raza", "mascota.raza.especie", "mascota.propietario",
            "loteVacuna", "loteVacuna.vacuna", "veterinario"
    })
    @Query("""
        SELECT r FROM RegistroVacunacion r
        WHERE r.mascota.id = :mascotaId
          AND (:incluirAnuladas = true OR r.estadoRegistro = 'ACTIVO')
        ORDER BY r.fechaAplicacion ASC
    """)
    List<RegistroVacunacion> findParaCarnet(
            @Param("mascotaId") UUID mascotaId,
            @Param("incluirAnuladas") boolean incluirAnuladas
    );
}
