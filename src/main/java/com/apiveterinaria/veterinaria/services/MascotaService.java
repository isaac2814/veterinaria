package com.apiveterinaria.veterinaria.services;

import com.apiveterinaria.veterinaria.config.ContextoAuditoria;
import com.apiveterinaria.veterinaria.config.PaginacionUtil;
import com.apiveterinaria.veterinaria.dto.ActualizarMascotaRequest;
import com.apiveterinaria.veterinaria.dto.CrearMascotaRequest;
import com.apiveterinaria.veterinaria.dto.MascotaResponse;
import com.apiveterinaria.veterinaria.enums.EstadoMascota;
import com.apiveterinaria.veterinaria.exception.RecursoNoEncontradoException;
import com.apiveterinaria.veterinaria.exception.ReglaNegocioException;
import com.apiveterinaria.veterinaria.models.Mascota;
import com.apiveterinaria.veterinaria.models.Propietario;
import com.apiveterinaria.veterinaria.models.Raza;
import com.apiveterinaria.veterinaria.repositories.MascotaRepository;
import com.apiveterinaria.veterinaria.repositories.PropietarioRepository;
import com.apiveterinaria.veterinaria.repositories.RazaRepository;

import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class MascotaService {

    private static final Set<String> CAMPOS_ORDEN_PERMITIDOS = Set.of(
            "id", "nombre", "sexo", "fechaNacimiento", "pesoKg", "estado", "createdAt", "updatedAt"
    );

    private final MascotaRepository mascotaRepository;
    private final PropietarioRepository propietarioRepository;
    private final RazaRepository razaRepository;
    private final ContextoAuditoria contextoAuditoria;
    private final EntityManager entityManager;

    public MascotaService(MascotaRepository mascotaRepository,
                          PropietarioRepository propietarioRepository,
                          RazaRepository razaRepository,
                          ContextoAuditoria contextoAuditoria,
                          EntityManager entityManager) {
        this.mascotaRepository = mascotaRepository;
        this.propietarioRepository = propietarioRepository;
        this.razaRepository = razaRepository;
        this.contextoAuditoria = contextoAuditoria;
        this.entityManager = entityManager;
    }

    @Transactional
    public MascotaResponse crear(CrearMascotaRequest request) {
        contextoAuditoria.aplicar();

        if (request.fechaNacimiento() != null && request.fechaNacimiento().isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha de nacimiento no puede ser futura");
        }

        Propietario propietario = propietarioRepository.findById(request.propietarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Propietario no encontrado con ID: " + request.propietarioId()));

        if (!Boolean.TRUE.equals(propietario.getActivo())) {
            throw new ReglaNegocioException("El propietario debe existir y estar activo para registrar una mascota");
        }

        Raza raza = razaRepository.findWithEspecieById(request.razaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Raza no encontrada con ID: " + request.razaId()));

        Mascota mascota = Mascota.builder()
                .propietario(propietario)
                .nombre(request.nombre().trim())
                .raza(raza)
                .sexo(request.sexo())
                .fechaNacimiento(request.fechaNacimiento())
                .pesoKg(request.pesoKg())
                .estado(EstadoMascota.ACTIVO)
                .build();

        mascota = mascotaRepository.save(mascota);
        entityManager.flush();
        entityManager.refresh(mascota);

        return MascotaResponse.from(mascota);
    }

    public Page<MascotaResponse> listar(UUID propietarioId, UUID especieId, String nombre, EstadoMascota estado, Pageable pageable) {
        Pageable pageableValido = PaginacionUtil.validarOrden(pageable, CAMPOS_ORDEN_PERMITIDOS, "createdAt");
        String filtroNombre = (nombre != null && !nombre.isBlank()) ? "%" + nombre.trim().toLowerCase() + "%" : null;

        return mascotaRepository.buscarMascotas(propietarioId, especieId, filtroNombre, estado, pageableValido)
                .map(MascotaResponse::from);
    }

    public MascotaResponse obtenerPorId(UUID id) {
        Mascota mascota = mascotaRepository.findWithDetallesById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada con ID: " + id));
        return MascotaResponse.from(mascota);
    }

    @Transactional
    public MascotaResponse actualizar(UUID id, ActualizarMascotaRequest request) {
        contextoAuditoria.aplicar();

        if (request.fechaNacimiento() != null && request.fechaNacimiento().isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha de nacimiento no puede ser futura");
        }

        Mascota mascota = mascotaRepository.findWithDetallesById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada con ID: " + id));

        Raza raza = razaRepository.findWithEspecieById(request.razaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Raza no encontrada con ID: " + request.razaId()));

        mascota.setNombre(request.nombre().trim());
        mascota.setRaza(raza);
        mascota.setSexo(request.sexo());
        mascota.setFechaNacimiento(request.fechaNacimiento());
        mascota.setPesoKg(request.pesoKg());

        mascota = mascotaRepository.save(mascota);
        entityManager.flush();
        entityManager.refresh(mascota);

        return MascotaResponse.from(mascota);
    }

    @Transactional
    public MascotaResponse cambiarEstado(UUID id, EstadoMascota estado) {
        contextoAuditoria.aplicar();

        Mascota mascota = mascotaRepository.findWithDetallesById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada con ID: " + id));

        mascota.setEstado(estado);
        mascota = mascotaRepository.save(mascota);
        entityManager.flush();
        entityManager.refresh(mascota);

        return MascotaResponse.from(mascota);
    }

    @Transactional
    public void desactivar(UUID id) {
        contextoAuditoria.aplicar();

        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada con ID: " + id));

        mascota.setEstado(EstadoMascota.INACTIVO);
        mascotaRepository.save(mascota);
        entityManager.flush();
    }
}
