package com.apiveterinaria.veterinaria.repositories;

import com.apiveterinaria.veterinaria.enums.CanalNotificacion;
import com.apiveterinaria.veterinaria.models.Notificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, UUID> {

    @EntityGraph(attributePaths = {"estado"})
    List<Notificacion> findByRegistroVacunacionIdOrderByCreatedAtAsc(UUID registroVacunacionId);

    @EntityGraph(attributePaths = {"registroVacunacion", "estado"})
    @Query("SELECT n FROM Notificacion n WHERE n.id = :id")
    Optional<Notificacion> findByIdWithRelaciones(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"registroVacunacion", "estado"})
    @Query("""
        SELECT n FROM Notificacion n
        WHERE (:registroVacunacionId IS NULL OR n.registroVacunacion.id = :registroVacunacionId)
          AND (:estadoId IS NULL OR n.estado.id = :estadoId)
          AND (:canal IS NULL OR n.canal = :canal)
    """)
    Page<Notificacion> findConFiltros(
            @Param("registroVacunacionId") UUID registroVacunacionId,
            @Param("estadoId") UUID estadoId,
            @Param("canal") CanalNotificacion canal,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"registroVacunacion", "estado"})
    @Query("""
        SELECT n FROM Notificacion n
        WHERE n.estado.codigo = 'PENDIENTE'
          AND (n.enviarEn IS NULL OR n.enviarEn <= :ahora)
        ORDER BY n.enviarEn ASC NULLS FIRST
    """)
    List<Notificacion> findCola(@Param("ahora") OffsetDateTime ahora);
}
