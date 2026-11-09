package com.apiveterinaria.veterinaria.services;

import com.apiveterinaria.veterinaria.config.ContextoAuditoria;
import com.apiveterinaria.veterinaria.config.PaginacionUtil;
import com.apiveterinaria.veterinaria.dto.ActualizarVacunaRequest;
import com.apiveterinaria.veterinaria.dto.CrearVacunaRequest;
import com.apiveterinaria.veterinaria.dto.VacunaResponse;
import com.apiveterinaria.veterinaria.exception.RecursoNoEncontradoException;
import com.apiveterinaria.veterinaria.models.Vacuna;
import com.apiveterinaria.veterinaria.repositories.VacunaRepository;

import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class VacunaService {

    private static final Set<String> CAMPOS_ORDEN_PERMITIDOS = Set.of(
            "id", "codigo", "nombre", "fabricante", "createdAt", "updatedAt"
    );

    private final VacunaRepository vacunaRepository;
    private final ContextoAuditoria contextoAuditoria;
    private final EntityManager entityManager;

    public VacunaService(VacunaRepository vacunaRepository,
                         ContextoAuditoria contextoAuditoria,
                         EntityManager entityManager) {
        this.vacunaRepository = vacunaRepository;
        this.contextoAuditoria = contextoAuditoria;
        this.entityManager = entityManager;
    }

    @Transactional
    public VacunaResponse crear(CrearVacunaRequest request) {
        contextoAuditoria.aplicar();

        String codigoNormalizado = request.codigo().trim().toUpperCase();
        if (vacunaRepository.existsByCodigoIgnoreCase(codigoNormalizado)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "El código de vacuna '" + codigoNormalizado + "' ya se encuentra registrado");
        }

        Vacuna vacuna = Vacuna.builder()
                .codigo(codigoNormalizado)
                .nombre(request.nombre().trim())
                .fabricante(request.fabricante() != null && !request.fabricante().isBlank() ? request.fabricante().trim() : null)
                .descripcion(request.descripcion() != null && !request.descripcion().isBlank() ? request.descripcion().trim() : null)
                .build();

        vacuna = vacunaRepository.save(vacuna);
        entityManager.flush();
        entityManager.refresh(vacuna);

        return VacunaResponse.from(vacuna);
    }

    public Page<VacunaResponse> listar(String q, Pageable pageable) {
        Pageable pageableValido = PaginacionUtil.validarOrden(pageable, CAMPOS_ORDEN_PERMITIDOS, "nombre");
        String filtroQ = (q != null && !q.isBlank()) ? "%" + q.trim().toLowerCase() + "%" : null;

        return vacunaRepository.buscarVacunas(filtroQ, pageableValido)
                .map(VacunaResponse::from);
    }

    public VacunaResponse obtenerPorId(UUID id) {
        Vacuna vacuna = vacunaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vacuna no encontrada con ID: " + id));
        return VacunaResponse.from(vacuna);
    }

    @Transactional
    public VacunaResponse actualizar(UUID id, ActualizarVacunaRequest request) {
        contextoAuditoria.aplicar();

        Vacuna vacuna = vacunaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vacuna no encontrada con ID: " + id));

        vacuna.setCodigo(request.codigo().trim().toUpperCase());
        vacuna.setNombre(request.nombre().trim());
        vacuna.setFabricante(request.fabricante() != null && !request.fabricante().isBlank() ? request.fabricante().trim() : null);
        vacuna.setDescripcion(request.descripcion() != null && !request.descripcion().isBlank() ? request.descripcion().trim() : null);

        vacuna = vacunaRepository.save(vacuna);
        entityManager.flush();
        entityManager.refresh(vacuna);

        return VacunaResponse.from(vacuna);
    }

    @Transactional
    public void eliminar(UUID id) {
        contextoAuditoria.aplicar();

        Vacuna vacuna = vacunaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Vacuna no encontrada con ID: " + id));

        vacunaRepository.delete(vacuna);
        entityManager.flush();
    }
}
