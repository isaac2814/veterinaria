package com.apiveterinaria.veterinaria.services;

import com.apiveterinaria.veterinaria.config.ContextoAuditoria;
import com.apiveterinaria.veterinaria.config.PaginacionUtil;
import com.apiveterinaria.veterinaria.dto.*;
import com.apiveterinaria.veterinaria.enums.EstadoRefuerzo;
import com.apiveterinaria.veterinaria.enums.EstadoRegistro;
import com.apiveterinaria.veterinaria.exception.RecursoNoEncontradoException;
import com.apiveterinaria.veterinaria.models.*;
import com.apiveterinaria.veterinaria.repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class RegistroVacunacionService {

    private static final Set<String> CAMPOS_ORDEN_PERMITIDOS = Set.of(
            "fechaAplicacion", "createdAt", "numeroDosis", "fechaProximoRefuerzo"
    );

    private final RegistroVacunacionRepository registroVacunacionRepository;
    private final MascotaRepository mascotaRepository;
    private final LoteVacunaRepository loteVacunaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DosisEsquemaRepository dosisEsquemaRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final NotificacionRepository notificacionRepository;
    private final ContextoAuditoria contextoAuditoria;

    @PersistenceContext
    private final EntityManager entityManager;

    public RegistroVacunacionService(
            RegistroVacunacionRepository registroVacunacionRepository,
            MascotaRepository mascotaRepository,
            LoteVacunaRepository loteVacunaRepository,
            UsuarioRepository usuarioRepository,
            DosisEsquemaRepository dosisEsquemaRepository,
            MovimientoInventarioRepository movimientoInventarioRepository,
            NotificacionRepository notificacionRepository,
            ContextoAuditoria contextoAuditoria,
            EntityManager entityManager
    ) {
        this.registroVacunacionRepository = registroVacunacionRepository;
        this.mascotaRepository = mascotaRepository;
        this.loteVacunaRepository = loteVacunaRepository;
        this.usuarioRepository = usuarioRepository;
        this.dosisEsquemaRepository = dosisEsquemaRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
        this.notificacionRepository = notificacionRepository;
        this.contextoAuditoria = contextoAuditoria;
        this.entityManager = entityManager;
    }

    @Transactional
    public RegistroVacunacionResponse crear(CrearRegistroVacunacionRequest request) {
        contextoAuditoria.aplicar();

        Mascota mascota = mascotaRepository.findById(request.mascotaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada con ID: " + request.mascotaId()));

        LoteVacuna lote = loteVacunaRepository.findById(request.loteVacunaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Lote de vacuna no encontrado con ID: " + request.loteVacunaId()));

        Usuario veterinario = usuarioRepository.findById(request.veterinarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Veterinario no encontrado con ID: " + request.veterinarioId()));

        DosisEsquema dosisEsquema = null;
        if (request.dosisEsquemaId() != null) {
            dosisEsquema = dosisEsquemaRepository.findById(request.dosisEsquemaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Dosis de esquema no encontrada con ID: " + request.dosisEsquemaId()));

            if (registroVacunacionRepository.existsByMascotaIdAndDosisEsquemaIdAndEstadoRegistro(
                    mascota.getId(), dosisEsquema.getId(), EstadoRegistro.ACTIVO)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Esa dosis ya fue aplicada a la mascota");
            }
        }

        RegistroVacunacion dosisPrevia = null;
        if (request.dosisPreviaId() != null) {
            dosisPrevia = registroVacunacionRepository.findById(request.dosisPreviaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Dosis previa no encontrada con ID: " + request.dosisPreviaId()));
        }

        RegistroVacunacion registro = RegistroVacunacion.builder()
                .mascota(mascota)
                .loteVacuna(lote)
                .veterinario(veterinario)
                .dosisEsquema(dosisEsquema)
                .dosisPrevia(dosisPrevia)
                .fechaAplicacion(request.fechaAplicacion())
                .fechaProximoRefuerzo(request.fechaProximoRefuerzo())
                .intervaloRefuerzoValor(request.intervaloRefuerzoValor())
                .intervaloRefuerzoUnidad(request.intervaloRefuerzoUnidad())
                .observaciones(request.observaciones() != null ? request.observaciones().trim() : null)
                .build();

        registro = registroVacunacionRepository.saveAndFlush(registro);
        entityManager.clear();

        registro = registroVacunacionRepository.findByIdWithRelaciones(registro.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Registro de vacunación no encontrado tras persistencia"));

        return RegistroVacunacionResponse.from(registro);
    }

    @Transactional(readOnly = true)
    public Page<RegistroVacunacionResponse> listar(
            UUID mascotaId,
            UUID vacunaId,
            UUID veterinarioId,
            EstadoRegistro estado,
            EstadoRefuerzo estadoRefuerzo,
            LocalDate desde,
            LocalDate hasta,
            Pageable pageable
    ) {
        Pageable pageableValido = PaginacionUtil.validarOrden(pageable, CAMPOS_ORDEN_PERMITIDOS, "fechaAplicacion");
        return registroVacunacionRepository.findConFiltros(
                mascotaId, vacunaId, veterinarioId, estado, estadoRefuerzo, desde, hasta, pageableValido
        ).map(RegistroVacunacionResponse::from);
    }

    @Transactional(readOnly = true)
    public RegistroVacunacionDetalleResponse obtenerPorId(UUID id) {
        RegistroVacunacion registro = registroVacunacionRepository.findByIdWithRelaciones(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Registro de vacunación no encontrado con ID: " + id));

        List<MovimientoInventario> movimientos = movimientoInventarioRepository.findByRegistroVacunacionId(id);
        List<MovimientoResponse> movDtos = movimientos.stream()
                .map(MovimientoResponse::from)
                .toList();

        List<Notificacion> notificaciones = notificacionRepository.findByRegistroVacunacionIdOrderByCreatedAtAsc(id);
        List<NotificacionResumenResponse> notifDtos = notificaciones.stream()
                .map(NotificacionResumenResponse::from)
                .toList();

        return RegistroVacunacionDetalleResponse.of(registro, movDtos, notifDtos);
    }

    @Transactional
    public RegistroVacunacionResponse actualizarObservaciones(UUID id, ActualizarObservacionesRequest request) {
        contextoAuditoria.aplicar();

        RegistroVacunacion registro = registroVacunacionRepository.findByIdWithRelaciones(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Registro de vacunación no encontrado con ID: " + id));

        registro.setObservaciones(request.observaciones() != null ? request.observaciones().trim() : null);
        registro = registroVacunacionRepository.saveAndFlush(registro);

        return RegistroVacunacionResponse.from(registro);
    }

    @Transactional
    public RegistroVacunacionResponse anular(UUID id, AnularRegistroRequest request) {
        contextoAuditoria.aplicar();

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + request.usuarioId()));

        int filasAfectadas = registroVacunacionRepository.anular(id, request.motivo().trim(), usuario.getId());

        if (filasAfectadas == 0) {
            if (!registroVacunacionRepository.existsById(id)) {
                throw new RecursoNoEncontradoException("Registro de vacunación no encontrado con ID: " + id);
            } else {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El registro de vacunación ya se encuentra anulado");
            }
        }

        entityManager.clear();

        RegistroVacunacion registro = registroVacunacionRepository.findByIdWithRelaciones(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Registro de vacunación no encontrado tras anulación"));

        return RegistroVacunacionResponse.from(registro);
    }

    @Transactional
    public void eliminar(UUID id) {
        contextoAuditoria.aplicar();

        RegistroVacunacion registro = registroVacunacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Registro de vacunación no encontrado con ID: " + id));

        registroVacunacionRepository.delete(registro);
        entityManager.flush();
    }
}
