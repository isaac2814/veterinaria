package com.apiveterinaria.veterinaria.repositories;

import com.apiveterinaria.veterinaria.models.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RolRepository extends JpaRepository<Rol, UUID> {

    Optional<Rol> findByCodigo(String codigo);

    List<Rol> findAllByOrderByNombreAsc();

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, UUID id);

    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, UUID id);
}
