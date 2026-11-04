package com.apiveterinaria.veterinaria.repositories;

import com.apiveterinaria.veterinaria.models.Propietario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PropietarioRepository extends JpaRepository<Propietario, UUID> {

    Optional<Propietario> findByDocumento(String documento);

    boolean existsByDocumento(String documento);

    @Query(value = "SELECT p FROM Propietario p WHERE " +
            "(:activo IS NULL OR p.activo = :activo) AND " +
            "(:q IS NULL OR LOWER(p.documento) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "             OR LOWER(p.nombres) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "             OR LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "             OR LOWER(p.telefonoWhatsapp) LIKE LOWER(CONCAT('%', :q, '%')))",
            countQuery = "SELECT COUNT(p) FROM Propietario p WHERE " +
            "(:activo IS NULL OR p.activo = :activo) AND " +
            "(:q IS NULL OR LOWER(p.documento) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "             OR LOWER(p.nombres) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "             OR LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "             OR LOWER(p.telefonoWhatsapp) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Propietario> buscarPropietarios(@Param("activo") Boolean activo,
                                         @Param("q") String q,
                                         Pageable pageable);
}
