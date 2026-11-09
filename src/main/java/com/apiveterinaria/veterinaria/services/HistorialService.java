package com.apiveterinaria.veterinaria.services;

import com.apiveterinaria.veterinaria.config.PaginacionUtil;
import com.apiveterinaria.veterinaria.dto.HistorialResponse;
import com.apiveterinaria.veterinaria.exception.RecursoNoEncontradoException;
import com.apiveterinaria.veterinaria.models.RegistroHistorico;
import com.apiveterinaria.veterinaria.repositories.RegistroHistoricoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class HistorialService {

    private static final ZoneId ZONA_BOGOTA = ZoneId.of("America/Bogota");
    private static final Set<String> CAMPOS_ORDEN_PERMITIDOS = Set.of(
            "fechaEvento", "createdAt", "accion", "entidad"
    );

    private final RegistroHistoricoRepository registroHistoricoRepository;

    public HistorialService(RegistroHistoricoRepository registroHistoricoRepository) {
        this.registroHistoricoRepository = registroHistoricoRepository;
    }

    public Page<HistorialResponse> listar(
            String entidad,
            UUID entidadId,
            String accion,
            LocalDate desde,
            LocalDate hasta,
            Pageable pageable
    ) {
        OffsetDateTime desdeOffset = desde != null ? desde.atStartOfDay(ZONA_BOGOTA).toOffsetDateTime() : null;
        OffsetDateTime hastaOffset = hasta != null ? hasta.plusDays(1).atStartOfDay(ZONA_BOGOTA).minusNanos(1).toOffsetDateTime() : null;

        String entidadFiltro = (entidad != null && !entidad.isBlank()) ? entidad.trim().toLowerCase() : null;
        String accionFiltro = (accion != null && !accion.isBlank()) ? accion.trim().toLowerCase() : null;

        Pageable pageableValido = PaginacionUtil.validarOrden(pageable, CAMPOS_ORDEN_PERMITIDOS, "fechaEvento");

        return registroHistoricoRepository.findConFiltros(
                entidadFiltro,
                entidadId,
                accionFiltro,
                desdeOffset,
                hastaOffset,
                pageableValido
        ).map(HistorialResponse::from);
    }

    public HistorialResponse obtenerPorId(UUID id) {
        RegistroHistorico historico = registroHistoricoRepository.findByIdWithRelaciones(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Registro histórico no encontrado con ID: " + id));
        return HistorialResponse.from(historico);
    }
}
