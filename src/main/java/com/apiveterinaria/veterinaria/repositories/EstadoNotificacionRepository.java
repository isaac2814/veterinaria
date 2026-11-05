package com.apiveterinaria.veterinaria.repositories;

import com.apiveterinaria.veterinaria.models.EstadoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EstadoNotificacionRepository extends JpaRepository<EstadoNotificacion, UUID> {

    Optional<EstadoNotificacion> findByCodigo(String codigo);

    List<EstadoNotificacion> findAllByOrderByNombreAsc();

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByCodigoIgnoreCase(String codigo);
}
