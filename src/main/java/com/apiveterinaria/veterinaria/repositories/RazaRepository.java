package com.apiveterinaria.veterinaria.repositories;

import com.apiveterinaria.veterinaria.models.Raza;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RazaRepository extends JpaRepository<Raza, UUID> {

    @EntityGraph(attributePaths = {"especie"})
    List<Raza> findByEspecieIdOrderByNombreAsc(UUID especieId);

    @EntityGraph(attributePaths = {"especie"})
    List<Raza> findAllByOrderByNombreAsc();

    @EntityGraph(attributePaths = {"especie"})
    Optional<Raza> findWithEspecieById(UUID id);

    boolean existsByEspecieIdAndNombreIgnoreCase(UUID especieId, String nombre);

    boolean existsByEspecieIdAndNombreIgnoreCaseAndIdNot(UUID especieId, String nombre, UUID id);

    boolean existsByEspecieId(UUID especieId);

    Optional<Raza> findByEspecieIdAndNombreIgnoreCase(UUID especieId, String nombre);
}
