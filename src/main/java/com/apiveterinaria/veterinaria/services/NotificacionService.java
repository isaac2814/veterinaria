package com.apiveterinaria.veterinaria.services;

import com.apiveterinaria.veterinaria.config.ContextoAuditoria;
import com.apiveterinaria.veterinaria.dto.ActualizarNotificacionRequest;
import com.apiveterinaria.veterinaria.dto.CrearNotificacionRequest;
import com.apiveterinaria.veterinaria.dto.MarcarEnviadaRequest;
import com.apiveterinaria.veterinaria.dto.NotificacionDetalleResponse;
import com.apiveterinaria.veterinaria.enums.CanalNotificacion;
import com.apiveterinaria.veterinaria.exception.RecursoNoEncontradoException;
import com.apiveterinaria.veterinaria.models.EstadoNotificacion;
import com.apiveterinaria.veterinaria.models.Notificacion;
import com.apiveterinaria.veterinaria.models.RegistroVacunacion;
import com.apiveterinaria.veterinaria.repositories.EstadoNotificacionRepository;
import com.apiveterinaria.veterinaria.repositories.NotificacionRepository;
import com.apiveterinaria.veterinaria.repositories.RegistroVacunacionRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final RegistroVacunacionRepository registroVacunacionRepository;
    private final EstadoNotificacionRepository estadoNotificacionRepository;
    private final ContextoAuditoria contextoAuditoria;
    private final EntityManager entityManager;

    public NotificacionService(
            NotificacionRepository notificacionRepository,
            RegistroVacunacionRepository registroVacunacionRepository,
            EstadoNotificacionRepository estadoNotificacionRepository,
            ContextoAuditoria contextoAuditoria,
            EntityManager entityManager
    ) {
        this.notificacionRepository = notificacionRepository;
        this.registroVacunacionRepository = registroVacunacionRepository;
        this.estadoNotificacionRepository = estadoNotificacionRepository;
        this.contextoAuditoria = contextoAuditoria;
        this.entityManager = entityManager;
    }

    public Page<NotificacionDetalleResponse> listar(
            UUID registroVacunacionId,
            UUID estadoId,
            CanalNotificacion canal,
            Pageable pageable
    ) {
        return notificacionRepository.findConFiltros(registroVacunacionId, estadoId, canal, pageable)
                .map(NotificacionDetalleResponse::from);
    }

    public NotificacionDetalleResponse obtenerPorId(UUID id) {
        Notificacion notificacion = notificacionRepository.findByIdWithRelaciones(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificación no encontrada con ID: " + id));
        return NotificacionDetalleResponse.from(notificacion);
    }

    public List<NotificacionDetalleResponse> listarCola() {
        return notificacionRepository.findCola(OffsetDateTime.now())
                .stream()
                .map(NotificacionDetalleResponse::from)
                .toList();
    }

    @Transactional
    public NotificacionDetalleResponse crear(CrearNotificacionRequest request) {
        contextoAuditoria.aplicar();

        RegistroVacunacion registro = registroVacunacionRepository.findById(request.registroVacunacionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Registro de vacunación no encontrado con ID: " + request.registroVacunacionId()));

        EstadoNotificacion estadoPendiente = estadoNotificacionRepository.findByCodigo("PENDIENTE")
                .orElseGet(() -> estadoNotificacionRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new RecursoNoEncontradoException("Estado PENDIENTE no configurado en el catálogo")));

        Notificacion notificacion = Notificacion.builder()
                .registroVacunacion(registro)
                .estado(estadoPendiente)
                .canal(request.canal())
                .mensaje(request.mensaje().trim())
                .intentos(0)
                .enviarEn(request.enviarEn() != null ? request.enviarEn() : OffsetDateTime.now())
                .build();

        notificacion = notificacionRepository.save(notificacion);
        entityManager.flush();
        entityManager.refresh(notificacion);

        return NotificacionDetalleResponse.from(notificacion);
    }

    @Transactional
    public NotificacionDetalleResponse marcarEnviada(UUID id, MarcarEnviadaRequest request) {
        contextoAuditoria.aplicar();

        Notificacion notificacion = notificacionRepository.findByIdWithRelaciones(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificación no encontrada con ID: " + id));

        EstadoNotificacion estadoEnviado = estadoNotificacionRepository.findByCodigo("ENVIADO")
                .orElseThrow(() -> new RecursoNoEncontradoException("Estado ENVIADO no configurado en el catálogo"));

        notificacion.setEstado(estadoEnviado);
        notificacion.setFechaEnvio(OffsetDateTime.now());
        notificacion.setIntentos((notificacion.getIntentos() != null ? notificacion.getIntentos() : 0) + 1);

        if (request != null && request.idMensajeProveedor() != null && !request.idMensajeProveedor().isBlank()) {
            notificacion.setIdMensajeProveedor(request.idMensajeProveedor().trim());
        }

        notificacion = notificacionRepository.save(notificacion);
        entityManager.flush();
        entityManager.refresh(notificacion);

        return NotificacionDetalleResponse.from(notificacion);
    }

    @Transactional
    public NotificacionDetalleResponse actualizar(UUID id, ActualizarNotificacionRequest request) {
        contextoAuditoria.aplicar();

        Notificacion notificacion = notificacionRepository.findByIdWithRelaciones(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificación no encontrada con ID: " + id));

        if (request.mensaje() != null && !request.mensaje().isBlank()) {
            notificacion.setMensaje(request.mensaje().trim());
        }
        if (request.enviarEn() != null) {
            notificacion.setEnviarEn(request.enviarEn());
        }
        if (request.canal() != null) {
            notificacion.setCanal(request.canal());
        }
        if (request.estadoId() != null) {
            EstadoNotificacion nuevoEstado = estadoNotificacionRepository.findById(request.estadoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Estado de notificación no encontrado con ID: " + request.estadoId()));
            notificacion.setEstado(nuevoEstado);
        }

        notificacion = notificacionRepository.save(notificacion);
        entityManager.flush();
        entityManager.refresh(notificacion);

        return NotificacionDetalleResponse.from(notificacion);
    }

    @Transactional
    public void eliminar(UUID id) {
        contextoAuditoria.aplicar();

        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificación no encontrada con ID: " + id));

        notificacionRepository.delete(notificacion);
        entityManager.flush();
    }
}
