package com.apiveterinaria.veterinaria.services;

import com.apiveterinaria.veterinaria.config.ContextoAuditoria;
import com.apiveterinaria.veterinaria.dto.ActualizarDosisRequest;
import com.apiveterinaria.veterinaria.dto.CrearDosisRequest;
import com.apiveterinaria.veterinaria.dto.DosisResponse;
import com.apiveterinaria.veterinaria.exception.RecursoNoEncontradoException;
import com.apiveterinaria.veterinaria.models.DosisEsquema;
import com.apiveterinaria.veterinaria.models.EsquemaVacunacion;
import com.apiveterinaria.veterinaria.repositories.DosisEsquemaRepository;
import com.apiveterinaria.veterinaria.repositories.EsquemaVacunacionRepository;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class DosisEsquemaService {

    private final DosisEsquemaRepository dosisEsquemaRepository;
    private final EsquemaVacunacionRepository esquemaVacunacionRepository;
    private final ContextoAuditoria contextoAuditoria;
    private final EntityManager entityManager;

    public DosisEsquemaService(DosisEsquemaRepository dosisEsquemaRepository,
                               EsquemaVacunacionRepository esquemaVacunacionRepository,
                               ContextoAuditoria contextoAuditoria,
                               EntityManager entityManager) {
        this.dosisEsquemaRepository = dosisEsquemaRepository;
        this.esquemaVacunacionRepository = esquemaVacunacionRepository;
        this.contextoAuditoria = contextoAuditoria;
        this.entityManager = entityManager;
    }

    @Transactional
    public DosisResponse crear(UUID esquemaId, CrearDosisRequest request) {
        contextoAuditoria.aplicar();

        EsquemaVacunacion esquema = esquemaVacunacionRepository.findById(esquemaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Esquema de vacunación no encontrado con ID: " + esquemaId));

        DosisEsquema dosis = DosisEsquema.builder()
                .esquema(esquema)
                .numeroDosis(request.numeroDosis())
                .intervaloCantidad(request.intervaloCantidad())
                .intervaloUnidad(request.intervaloUnidad())
                .edadRecomendadaValor(request.edadRecomendadaValor())
                .edadRecomendadaUnidad(request.edadRecomendadaUnidad())
                .esRefuerzo(request.esRefuerzo() != null ? request.esRefuerzo() : false)
                .observaciones(request.observaciones() != null && !request.observaciones().isBlank() ? request.observaciones().trim() : null)
                .build();

        dosis = dosisEsquemaRepository.save(dosis);
        entityManager.flush();
        entityManager.refresh(dosis);

        return DosisResponse.from(dosis);
    }

    public List<DosisResponse> listarPorEsquema(UUID esquemaId) {
        if (!esquemaVacunacionRepository.existsById(esquemaId)) {
            throw new RecursoNoEncontradoException("Esquema de vacunación no encontrado con ID: " + esquemaId);
        }

        return dosisEsquemaRepository.findByEsquemaIdOrderByNumeroDosisAsc(esquemaId)
                .stream()
                .map(DosisResponse::from)
                .toList();
    }

    public DosisResponse obtenerPorId(UUID esquemaId, UUID dosisId) {
        DosisEsquema dosis = dosisEsquemaRepository.findById(dosisId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Dosis no encontrada con ID: " + dosisId));

        if (!dosis.getEsquema().getId().equals(esquemaId)) {
            throw new RecursoNoEncontradoException("La dosis no pertenece al esquema especificado: " + esquemaId);
        }

        return DosisResponse.from(dosis);
    }

    @Transactional
    public DosisResponse actualizar(UUID esquemaId, UUID dosisId, ActualizarDosisRequest request) {
        contextoAuditoria.aplicar();

        DosisEsquema dosis = dosisEsquemaRepository.findById(dosisId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Dosis no encontrada con ID: " + dosisId));

        if (!dosis.getEsquema().getId().equals(esquemaId)) {
            throw new RecursoNoEncontradoException("La dosis no pertenece al esquema especificado: " + esquemaId);
        }

        dosis.setIntervaloCantidad(request.intervaloCantidad());
        dosis.setIntervaloUnidad(request.intervaloUnidad());
        dosis.setEdadRecomendadaValor(request.edadRecomendadaValor());
        dosis.setEdadRecomendadaUnidad(request.edadRecomendadaUnidad());
        if (request.esRefuerzo() != null) {
            dosis.setEsRefuerzo(request.esRefuerzo());
        }
        dosis.setObservaciones(request.observaciones() != null && !request.observaciones().isBlank() ? request.observaciones().trim() : null);

        dosis = dosisEsquemaRepository.save(dosis);
        entityManager.flush();
        entityManager.refresh(dosis);

        return DosisResponse.from(dosis);
    }

    @Transactional
    public void eliminar(UUID esquemaId, UUID dosisId) {
        contextoAuditoria.aplicar();

        DosisEsquema dosis = dosisEsquemaRepository.findById(dosisId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Dosis no encontrada con ID: " + dosisId));

        if (!dosis.getEsquema().getId().equals(esquemaId)) {
            throw new RecursoNoEncontradoException("La dosis no pertenece al esquema especificado: " + esquemaId);
        }

        dosisEsquemaRepository.delete(dosis);
        entityManager.flush();
    }
}
