package com.apiveterinaria.veterinaria.services;

import com.apiveterinaria.veterinaria.config.ContextoAuditoria;
import com.apiveterinaria.veterinaria.dto.*;
import com.apiveterinaria.veterinaria.exception.RecursoNoEncontradoException;
import com.apiveterinaria.veterinaria.models.Especie;
import com.apiveterinaria.veterinaria.models.Raza;
import com.apiveterinaria.veterinaria.repositories.EspecieRepository;
import com.apiveterinaria.veterinaria.repositories.EstadoNotificacionRepository;
import com.apiveterinaria.veterinaria.repositories.RazaRepository;
import com.apiveterinaria.veterinaria.repositories.RolRepository;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CatalogoService {

    private final RolRepository rolRepository;
    private final EspecieRepository especieRepository;
    private final RazaRepository razaRepository;
    private final EstadoNotificacionRepository estadoNotificacionRepository;
    private final ContextoAuditoria contextoAuditoria;
    private final EntityManager entityManager;

    public CatalogoService(RolRepository rolRepository,
                           EspecieRepository especieRepository,
                           RazaRepository razaRepository,
                           EstadoNotificacionRepository estadoNotificacionRepository,
                           ContextoAuditoria contextoAuditoria,
                           EntityManager entityManager) {
        this.rolRepository = rolRepository;
        this.especieRepository = especieRepository;
        this.razaRepository = razaRepository;
        this.estadoNotificacionRepository = estadoNotificacionRepository;
        this.contextoAuditoria = contextoAuditoria;
        this.entityManager = entityManager;
    }

    public List<RolResponse> listarRoles() {
        return rolRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(RolResponse::from)
                .toList();
    }

    public List<EspecieResponse> listarEspecies() {
        return especieRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(EspecieResponse::from)
                .toList();
    }

    public List<RazaResponse> listarRazas(UUID especieId) {
        List<Raza> razas = (especieId != null)
                ? razaRepository.findByEspecieIdOrderByNombreAsc(especieId)
                : razaRepository.findAllByOrderByNombreAsc();

        return razas.stream()
                .map(RazaResponse::from)
                .toList();
    }

    @Transactional
    public RazaResponse crearRaza(CrearRazaRequest request) {
        contextoAuditoria.aplicar();

        Especie especie = especieRepository.findById(request.especieId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Especie no encontrada con ID: " + request.especieId()));

        Raza raza = Raza.builder()
                .especie(especie)
                .nombre(request.nombre().trim())
                .descripcion(request.descripcion() != null ? request.descripcion().trim() : null)
                .build();

        raza = razaRepository.save(raza);
        entityManager.flush();
        entityManager.refresh(raza);

        return RazaResponse.from(raza);
    }

    public List<EstadoNotificacionResponse> listarEstadosNotificacion() {
        return estadoNotificacionRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(EstadoNotificacionResponse::from)
                .toList();
    }
}
