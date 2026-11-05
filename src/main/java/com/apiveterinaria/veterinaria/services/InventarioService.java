package com.apiveterinaria.veterinaria.services;

import com.apiveterinaria.veterinaria.config.ContextoAuditoria;
import com.apiveterinaria.veterinaria.dto.BajaVencidosRequest;
import com.apiveterinaria.veterinaria.dto.BajaVencidosResponse;
import com.apiveterinaria.veterinaria.exception.RecursoNoEncontradoException;
import com.apiveterinaria.veterinaria.exception.ReglaNegocioException;
import com.apiveterinaria.veterinaria.models.Usuario;
import com.apiveterinaria.veterinaria.repositories.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventarioService {

    private final UsuarioRepository usuarioRepository;
    private final ContextoAuditoria contextoAuditoria;

    @PersistenceContext
    private final EntityManager entityManager;

    public InventarioService(UsuarioRepository usuarioRepository,
                             ContextoAuditoria contextoAuditoria,
                             EntityManager entityManager) {
        this.usuarioRepository = usuarioRepository;
        this.contextoAuditoria = contextoAuditoria;
        this.entityManager = entityManager;
    }

    @Transactional
    public BajaVencidosResponse darBajaLotesVencidos(BajaVencidosRequest request) {
        contextoAuditoria.aplicar();

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + request.usuarioId()));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new ReglaNegocioException("El usuario no está activo");
        }

        Number resultado = (Number) entityManager.createNativeQuery(
                "SELECT fn_dar_baja_lotes_vencidos(:usuarioId)"
        )
        .setParameter("usuarioId", request.usuarioId())
        .getSingleResult();

        entityManager.clear();

        return new BajaVencidosResponse(resultado != null ? resultado.intValue() : 0);
    }
}
