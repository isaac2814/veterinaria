package com.apiveterinaria.veterinaria.repositories;

import com.apiveterinaria.veterinaria.models.Vacuna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VacunaRepository extends JpaRepository<Vacuna, UUID> {

    Optional<Vacuna> findByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCase(String codigo);

    @Query(value = "SELECT v FROM Vacuna v WHERE " +
            "(:q IS NULL OR LOWER(v.codigo) LIKE :q " +
            "             OR LOWER(v.nombre) LIKE :q " +
            "             OR LOWER(v.fabricante) LIKE :q)",
            countQuery = "SELECT COUNT(v) FROM Vacuna v WHERE " +
            "(:q IS NULL OR LOWER(v.codigo) LIKE :q " +
            "             OR LOWER(v.nombre) LIKE :q " +
            "             OR LOWER(v.fabricante) LIKE :q)")
    Page<Vacuna> buscarVacunas(@Param("q") String q, Pageable pageable);
}
