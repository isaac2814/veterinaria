package com.apiveterinaria.veterinaria.controllers;

import com.apiveterinaria.veterinaria.dto.HistorialResponse;
import com.apiveterinaria.veterinaria.services.HistorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/historial")
@Tag(name = "Historial", description = "Endpoints para la consulta de auditoría y trazabilidad del sistema")
public class HistorialController {

    private final HistorialService historialService;

    public HistorialController(HistorialService historialService) {
        this.historialService = historialService;
    }

    @GetMapping
    @Operation(summary = "Consultar el historial y registro de eventos de auditoría")
    @ApiResponse(responseCode = "200", description = "Página de registros históricos obtenida exitosamente")
    public ResponseEntity<Page<HistorialResponse>> listar(
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) UUID entidadId,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Pageable pageable
    ) {
        return ResponseEntity.ok(historialService.listar(entidad, entidadId, accion, desde, hasta, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un registro histórico de auditoría por su ID")
    @ApiResponse(responseCode = "200", description = "Registro histórico obtenido exitosamente")
    @ApiResponse(responseCode = "404", description = "Registro histórico no encontrado")
    public ResponseEntity<HistorialResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(historialService.obtenerPorId(id));
    }
}
