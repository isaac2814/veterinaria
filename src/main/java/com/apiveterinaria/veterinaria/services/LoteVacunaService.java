package com.apiveterinaria.veterinaria.services;

import com.apiveterinaria.veterinaria.config.ContextoAuditoria;
import com.apiveterinaria.veterinaria.config.PaginacionUtil;
import com.apiveterinaria.veterinaria.dto.ActualizarLoteRequest;
import com.apiveterinaria.veterinaria.dto.CrearLoteRequest;
import com.apiveterinaria.veterinaria.dto.LoteResponse;
import com.apiveterinaria.veterinaria.exception.RecursoNoEncontradoException;
import com.apiveterinaria.veterinaria.exception.ReglaNegocioException;
import com.apiveterinaria.veterinaria.models.LoteVacuna;
import com.apiveterinaria.veterinaria.models.Usuario;
import com.apiveterinaria.veterinaria.models.Vacuna;
import com.apiveterinaria.veterinaria.repositories.LoteVacunaRepository;
import com.apiveterinaria.veterinaria.repositories.UsuarioRepository;
import com.apiveterinaria.veterinaria.repositories.VacunaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class LoteVacunaService {

    private static final ZoneId ZONA_BOGOTA = ZoneId.of("America/Bogota");
    private static final Set<String> CAMPOS_ORDEN_PERMITIDOS = Set.of(
            "numeroLote", "fechaVencimiento", "fechaIngreso", "cantidadDisponible", "createdAt"
    );

    private final LoteVacunaRepository loteRepository;
    private final VacunaRepository vacunaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContextoAuditoria contextoAuditoria;

    @PersistenceContext
    private final EntityManager entityManager;

    public LoteVacunaService(LoteVacunaRepository loteRepository,
                             VacunaRepository vacunaRepository,
                             UsuarioRepository usuarioRepository,
                             ContextoAuditoria contextoAuditoria,
                             EntityManager entityManager) {
        this.loteRepository = loteRepository;
        this.vacunaRepository = vacunaRepository;
        this.usuarioRepository = usuarioRepository;
        this.contextoAuditoria = contextoAuditoria;
        this.entityManager = entityManager;
    }

    @Transactional
    public LoteResponse crear(CrearLoteRequest request) {
        contextoAuditoria.aplicar();

        Vacuna vacuna = vacunaRepository.findById(request.vacunaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Vacuna no encontrada con ID: " + request.vacunaId()));

        Usuario registradoPor = usuarioRepository.findById(request.registradoPor())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + request.registradoPor()));

        if (!Boolean.TRUE.equals(registradoPor.getActivo())) {
            throw new ReglaNegocioException("El usuario que registra el lote no está activo");
        }

        LocalDate hoy = LocalDate.now(ZONA_BOGOTA);
        LocalDate fechaIngresoEfectiva = request.fechaIngreso() != null ? request.fechaIngreso() : hoy;

        if (request.fechaVencimiento().isBefore(fechaIngresoEfectiva)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de vencimiento debe ser igual o posterior a la fecha de ingreso");
        }

        if (loteRepository.existsByVacunaIdAndNumeroLote(vacuna.getId(), request.numeroLote().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un lote con el número '" + request.numeroLote().trim() + "' para esta vacuna");
        }

        LoteVacuna lote = LoteVacuna.builder()
                .vacuna(vacuna)
                .numeroLote(request.numeroLote().trim())
                .fechaVencimiento(request.fechaVencimiento())
                .fechaIngreso(request.fechaIngreso())
                .cantidadRecibida(request.cantidadRecibida())
                .unidadInventario(request.unidadInventario() != null && !request.unidadInventario().isBlank()
                        ? request.unidadInventario().trim()
                        : "DOSIS")
                .registradoPor(registradoPor)
                .build();

        lote = loteRepository.saveAndFlush(lote);
        entityManager.refresh(lote);

        return LoteResponse.from(lote, hoy);
    }

    @Transactional
    public LoteResponse actualizar(UUID id, ActualizarLoteRequest request) {
        contextoAuditoria.aplicar();

        LoteVacuna lote = loteRepository.findByIdWithRelaciones(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Lote no encontrado con ID: " + id));

        LocalDate hoy = LocalDate.now(ZONA_BOGOTA);
        LocalDate fechaIngreso = lote.getFechaIngreso() != null ? lote.getFechaIngreso() : hoy;

        if (request.fechaVencimiento().isBefore(fechaIngreso)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de vencimiento no puede ser anterior a la fecha de ingreso del lote");
        }

        if (loteRepository.existsByVacunaIdAndNumeroLoteAndIdNot(lote.getVacuna().getId(), request.numeroLote().trim(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un lote con el número '" + request.numeroLote().trim() + "' para esta vacuna");
        }

        lote.setNumeroLote(request.numeroLote().trim());
        lote.setFechaVencimiento(request.fechaVencimiento());
        if (request.unidadInventario() != null && !request.unidadInventario().isBlank()) {
            lote.setUnidadInventario(request.unidadInventario().trim());
        }

        lote = loteRepository.saveAndFlush(lote);
        return LoteResponse.from(lote, hoy);
    }

    @Transactional(readOnly = true)
    public LoteResponse obtenerPorId(UUID id) {
        LocalDate hoy = LocalDate.now(ZONA_BOGOTA);
        LoteVacuna lote = loteRepository.findByIdWithRelaciones(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Lote no encontrado con ID: " + id));
        return LoteResponse.from(lote, hoy);
    }

    @Transactional(readOnly = true)
    public Page<LoteResponse> listar(UUID vacunaId, Boolean soloConStock, Boolean vencidos, Pageable pageable) {
        LocalDate hoy = LocalDate.now(ZONA_BOGOTA);
        Pageable ordenado = PaginacionUtil.validarOrden(pageable, CAMPOS_ORDEN_PERMITIDOS, "createdAt");
        Page<LoteVacuna> page = loteRepository.findConFiltros(vacunaId, soloConStock, vencidos, hoy, ordenado);
        return page.map(l -> LoteResponse.from(l, hoy));
    }

    @Transactional(readOnly = true)
    public List<LoteResponse> listarPorVencer(int dias) {
        LocalDate hoy = LocalDate.now(ZONA_BOGOTA);
        LocalDate fechaLimite = hoy.plusDays(dias);
        return loteRepository.findPorVencer(hoy, fechaLimite)
                .stream()
                .map(l -> LoteResponse.from(l, hoy))
                .toList();
    }
}
