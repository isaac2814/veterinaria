package com.apiveterinaria.veterinaria.repositories;

import com.apiveterinaria.veterinaria.enums.EstadoMascota;
import com.apiveterinaria.veterinaria.models.Mascota;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, UUID> {

    @Query("SELECT m FROM Mascota m " +
            "JOIN FETCH m.propietario p " +
            "JOIN FETCH m.raza r " +
            "JOIN FETCH r.especie e " +
            "WHERE m.id = :id")
    Optional<Mascota> findWithDetallesById(@Param("id") UUID id);

    @Query(value = "SELECT m FROM Mascota m " +
            "JOIN FETCH m.propietario p " +
            "JOIN FETCH m.raza r " +
            "JOIN FETCH r.especie e " +
            "WHERE (:propietarioId IS NULL OR p.id = :propietarioId) " +
            "  AND (:especieId IS NULL OR e.id = :especieId) " +
            "  AND (:nombre IS NULL OR LOWER(m.nombre) LIKE :nombre) " +
            "  AND (:estado IS NULL OR m.estado = :estado)",
            countQuery = "SELECT COUNT(m) FROM Mascota m " +
            "WHERE (:propietarioId IS NULL OR m.propietario.id = :propietarioId) " +
            "  AND (:especieId IS NULL OR m.raza.especie.id = :especieId) " +
            "  AND (:nombre IS NULL OR LOWER(m.nombre) LIKE :nombre) " +
            "  AND (:estado IS NULL OR m.estado = :estado)")
    Page<Mascota> buscarMascotas(@Param("propietarioId") UUID propietarioId,
                                 @Param("especieId") UUID especieId,
                                 @Param("nombre") String nombre,
                                 @Param("estado") EstadoMascota estado,
                                 Pageable pageable);
}
