package com.apiveterinaria.veterinaria.repositories;

import com.apiveterinaria.veterinaria.models.Notificacion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, UUID> {

    @EntityGraph(attributePaths = {"estado"})
    List<Notificacion> findByRegistroVacunacionIdOrderByCreatedAtAsc(UUID registroVacunacionId);
}
