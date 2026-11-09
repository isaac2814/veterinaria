package com.apiveterinaria.veterinaria.repositories;

import com.apiveterinaria.veterinaria.models.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {"rol"})
    Optional<Usuario> findWithRolById(UUID id);

    @Query(value = "SELECT u FROM Usuario u JOIN FETCH u.rol r WHERE " +
            "(:rolId IS NULL OR r.id = :rolId) AND " +
            "(:activo IS NULL OR u.activo = :activo) AND " +
            "(:q IS NULL OR LOWER(u.nombre) LIKE :q " +
            "             OR LOWER(u.apellidos) LIKE :q " +
            "             OR LOWER(u.email) LIKE :q)",
            countQuery = "SELECT COUNT(u) FROM Usuario u WHERE " +
            "(:rolId IS NULL OR u.rol.id = :rolId) AND " +
            "(:activo IS NULL OR u.activo = :activo) AND " +
            "(:q IS NULL OR LOWER(u.nombre) LIKE :q " +
            "             OR LOWER(u.apellidos) LIKE :q " +
            "             OR LOWER(u.email) LIKE :q)")
    Page<Usuario> buscarUsuarios(@Param("rolId") UUID rolId,
                                 @Param("activo") Boolean activo,
                                 @Param("q") String q,
                                 Pageable pageable);
}
