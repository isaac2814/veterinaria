package com.apiveterinaria.veterinaria.services;

import com.apiveterinaria.veterinaria.config.ContextoAuditoria;
import com.apiveterinaria.veterinaria.config.PaginacionUtil;
import com.apiveterinaria.veterinaria.dto.CrearMovimientoRequest;
import com.apiveterinaria.veterinaria.dto.MovimientoResponse;
import com.apiveterinaria.veterinaria.enums.TipoMovimiento;
import com.apiveterinaria.veterinaria.exception.RecursoNoEncontradoException;
import com.apiveterinaria.veterinaria.exception.ReglaNegocioException;
import com.apiveterinaria.veterinaria.models.LoteVacuna;
import com.apiveterinaria.veterinaria.models.MovimientoInventario;
import com.apiveterinaria.veterinaria.models.Usuario;
import com.apiveterinaria.veterinaria.repositories.LoteVacunaRepository;
import com.apiveterinaria.veterinaria.repositories.MovimientoInventarioRepository;
import com.apiveterinaria.veterinaria.repositories.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;

@Service
public class MovimientoInventarioService {

    private static final ZoneId ZONA_BOGOTA = ZoneId.of("America/Bogota");
    private static final Set<String> CAMPOS_ORDEN_PERMITIDOS = Set.of(
            "fechaMovimiento", "cantidad", "createdAt"
    );

    private final MovimientoInventarioRepository movimientoRepository;
    private final LoteVacunaRepository loteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContextoAuditoria contextoAuditoria;

    @PersistenceContext
    private final EntityManager entityManager;

    public MovimientoInventarioService(MovimientoInventarioRepository movimientoRepository,
                                       LoteVacunaRepository loteRepository,
                                       UsuarioRepository usuarioRepository,
                                       ContextoAuditoria contextoAuditoria,
                                       EntityManager entityManager) {
        this.movimientoRepository = movimientoRepository;
        this.loteRepository = loteRepository;
        this.usuarioRepository = usuarioRepository;
        this.contextoAuditoria = contextoAuditoria;
        this.entityManager = entityManager;
    }

    @Transactional
    public MovimientoResponse crear(CrearMovimientoRequest request) {
        contextoAuditoria.aplicar();

        if (request.tipoMovimiento() != null && request.tipoMovimiento() != TipoMovimiento.AJUSTE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se permite el tipo de movimiento AJUSTE. Los tipos INGRESO, APLICACION, BAJA_VENCIMIENTO y REVERSION_ANULACION son generados automáticamente por la base de datos."
            );
        }

        if (request.cantidad().compareTo(BigDecimal.ZERO) == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad del movimiento no puede ser 0");
        }

        if (request.observaciones() == null || request.observaciones().trim().length() < 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Para un AJUSTE las observaciones son obligatorias (mínimo 5 caracteres)"
            );
        }

        if (request.idOperacion() != null && !request.idOperacion().isBlank()) {
            if (movimientoRepository.existsByIdOperacion(request.idOperacion().trim())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un movimiento con ese idOperacion");
            }
        }

        LoteVacuna lote = loteRepository.findByIdWithRelaciones(request.loteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Lote no encontrado con ID: " + request.loteId()));

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + request.usuarioId()));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new ReglaNegocioException("El usuario no está activo");
        }

        MovimientoInventario movimiento = MovimientoInventario.builder()
                .lote(lote)
                .tipoMovimiento(TipoMovimiento.AJUSTE)
                .cantidad(request.cantidad())
                .usuario(usuario)
                .observaciones(request.observaciones().trim())
                .idOperacion(request.idOperacion() != null && !request.idOperacion().isBlank()
                        ? request.idOperacion().trim()
                        : null)
                .build();

        movimiento = movimientoRepository.saveAndFlush(movimiento);
        entityManager.refresh(lote);

        return MovimientoResponse.from(movimiento);
    }

    @Transactional(readOnly = true)
    public MovimientoResponse obtenerPorId(UUID id) {
        MovimientoInventario movimiento = movimientoRepository.findByIdWithRelaciones(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Movimiento no encontrado con ID: " + id));
        return MovimientoResponse.from(movimiento);
    }

    @Transactional(readOnly = true)
    public Page<MovimientoResponse> listar(UUID loteId, TipoMovimiento tipo, LocalDate desde, LocalDate hasta, Pageable pageable) {
        Pageable ordenado = PaginacionUtil.validarOrden(pageable, CAMPOS_ORDEN_PERMITIDOS, "fechaMovimiento");

        OffsetDateTime desdeOffset = desde != null ? desde.atStartOfDay(ZONA_BOGOTA).toOffsetDateTime() : null;
        OffsetDateTime hastaOffset = hasta != null ? hasta.atTime(LocalTime.MAX).atZone(ZONA_BOGOTA).toOffsetDateTime() : null;

        return movimientoRepository.findConFiltros(loteId, tipo, desdeOffset, hastaOffset, ordenado)
                .map(MovimientoResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<MovimientoResponse> listarPorLote(UUID loteId, Pageable pageable) {
        if (!loteRepository.existsById(loteId)) {
            throw new RecursoNoEncontradoException("Lote no encontrado con ID: " + loteId);
        }
        Pageable ordenado = PaginacionUtil.validarOrden(pageable, CAMPOS_ORDEN_PERMITIDOS, "fechaMovimiento");
        return movimientoRepository.findByLoteId(loteId, ordenado)
                .map(MovimientoResponse::from);
    }
}
