package com.apiveterinaria.veterinaria.repositories;

import com.apiveterinaria.veterinaria.models.RefuerzoPendiente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

@org.springframework.stereotype.Repository
public interface RefuerzoPendienteRepository extends Repository<RefuerzoPendiente, UUID> {

    @Query("""
        SELECT r FROM RefuerzoPendiente r
        WHERE (CAST(:propietarioId AS uuid) IS NULL OR r.propietarioId = :propietarioId)
          AND (:soloAtrasados = true AND r.diasRestantes < 0
               OR (:soloAtrasados IS NULL OR :soloAtrasados = false) AND (:dias IS NULL OR r.diasRestantes <= :dias))
    """)
    Page<RefuerzoPendiente> findRefuerzos(
            @Param("dias") Integer dias,
            @Param("propietarioId") UUID propietarioId,
            @Param("soloAtrasados") Boolean soloAtrasados,
            Pageable pageable
    );

    @Query("SELECT r FROM RefuerzoPendiente r WHERE r.registroVacunacionId = :id")
    java.util.Optional<RefuerzoPendiente> findById(@Param("id") UUID id);
}
