package com.apiveterinaria.veterinaria.services;

import com.apiveterinaria.veterinaria.config.ContextoAuditoria;
import com.apiveterinaria.veterinaria.config.PaginacionUtil;
import com.apiveterinaria.veterinaria.dto.*;
import com.apiveterinaria.veterinaria.exception.RecursoNoEncontradoException;
import com.apiveterinaria.veterinaria.models.Especie;
import com.apiveterinaria.veterinaria.models.EsquemaVacunacion;
import com.apiveterinaria.veterinaria.models.Vacuna;
import com.apiveterinaria.veterinaria.repositories.DosisEsquemaRepository;
import com.apiveterinaria.veterinaria.repositories.EspecieRepository;
import com.apiveterinaria.veterinaria.repositories.EsquemaVacunacionRepository;
import com.apiveterinaria.veterinaria.repositories.VacunaRepository;

import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class EsquemaVacunacionService {

    private static final Set<String> CAMPOS_ORDEN_PERMITIDOS = Set.of(
            "id", "nombre", "version", "createdAt", "updatedAt"
    );

    private final EsquemaVacunacionRepository esquemaVacunacionRepository;
    private final VacunaRepository vacunaRepository;
    private final EspecieRepository especieRepository;
    private final DosisEsquemaRepository dosisEsquemaRepository;
    private final ContextoAuditoria contextoAuditoria;
    private final EntityManager entityManager;

    public EsquemaVacunacionService(EsquemaVacunacionRepository esquemaVacunacionRepository,
                                    VacunaRepository vacunaRepository,
                                    EspecieRepository especieRepository,
                                    DosisEsquemaRepository dosisEsquemaRepository,
                                    ContextoAuditoria contextoAuditoria,
                                    EntityManager entityManager) {
        this.esquemaVacunacionRepository = esquemaVacunacionRepository;
        this.vacunaRepository = vacunaRepository;
        this.especieRepository = especieRepository;
        this.dosisEsquemaRepository = dosisEsquemaRepository;
        this.contextoAuditoria = contextoAuditoria;
        this.entityManager = entityManager;
    }

    @Transactional
    public EsquemaDetalleResponse crear(CrearEsquemaRequest request) {
        contextoAuditoria.aplicar();

        Vacuna vacuna = vacunaRepository.findById(request.vacunaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Vacuna no encontrada con ID: " + request.vacunaId()));

        Especie especie = especieRepository.findById(request.especieId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Especie no encontrada con ID: " + request.especieId()));

        EsquemaVacunacion esquema = EsquemaVacunacion.builder()
                .vacuna(vacuna)
                .especie(especie)
                .nombre(request.nombre().trim())
                .edadInicialValor(request.edadInicialValor())
                .edadInicialUnidad(request.edadInicialUnidad())
                .observaciones(request.observaciones() != null && !request.observaciones().isBlank() ? request.observaciones().trim() : null)
                .build();

        esquema = esquemaVacunacionRepository.save(esquema);
        entityManager.flush();
        entityManager.refresh(esquema);

        return EsquemaDetalleResponse.from(esquema, List.of());
    }

    public Page<EsquemaListadoResponse> listar(UUID vacunaId, UUID especieId, Pageable pageable) {
        Pageable pageableValido = PaginacionUtil.validarOrden(pageable, CAMPOS_ORDEN_PERMITIDOS, "createdAt");
        return esquemaVacunacionRepository.buscarEsquemas(vacunaId, especieId, pageableValido)
                .map(EsquemaListadoResponse::from);
    }

    public EsquemaDetalleResponse obtenerPorId(UUID id) {
        EsquemaVacunacion esquema = esquemaVacunacionRepository.findWithDetallesById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Esquema de vacunación no encontrado con ID: " + id));

        List<DosisResponse> dosis = dosisEsquemaRepository.findByEsquemaIdOrderByNumeroDosisAsc(id)
                .stream()
                .map(DosisResponse::from)
                .toList();

        return EsquemaDetalleResponse.from(esquema, dosis);
    }

    @Transactional
    public EsquemaDetalleResponse actualizar(UUID id, ActualizarEsquemaRequest request) {
        contextoAuditoria.aplicar();

        EsquemaVacunacion esquema = esquemaVacunacionRepository.findWithDetallesById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Esquema de vacunación no encontrado con ID: " + id));

        esquema.setNombre(request.nombre().trim());
        esquema.setEdadInicialValor(request.edadInicialValor());
        esquema.setEdadInicialUnidad(request.edadInicialUnidad());
        esquema.setObservaciones(request.observaciones() != null && !request.observaciones().isBlank() ? request.observaciones().trim() : null);

        esquema = esquemaVacunacionRepository.save(esquema);
        entityManager.flush();
        entityManager.refresh(esquema);

        List<DosisResponse> dosis = dosisEsquemaRepository.findByEsquemaIdOrderByNumeroDosisAsc(id)
                .stream()
                .map(DosisResponse::from)
                .toList();

        return EsquemaDetalleResponse.from(esquema, dosis);
    }

    @Transactional
    public void eliminar(UUID id) {
        contextoAuditoria.aplicar();

        EsquemaVacunacion esquema = esquemaVacunacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Esquema de vacunación no encontrado con ID: " + id));

        esquemaVacunacionRepository.delete(esquema);
        entityManager.flush();
    }
}
