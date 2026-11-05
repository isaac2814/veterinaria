package com.apiveterinaria.veterinaria.controllers;

import com.apiveterinaria.veterinaria.dto.CrearNotificacionRequest;
import com.apiveterinaria.veterinaria.dto.MarcarEnviadaRequest;
import com.apiveterinaria.veterinaria.dto.NotificacionDetalleResponse;
import com.apiveterinaria.veterinaria.enums.CanalNotificacion;
import com.apiveterinaria.veterinaria.services.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones", description = "Endpoints para la consulta, programación y despacho de recordatorios y notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    @Operation(summary = "Listar notificaciones con filtros y paginación")
    @ApiResponse(responseCode = "200", description = "Página de notificaciones obtenida exitosamente")
    public ResponseEntity<Page<NotificacionDetalleResponse>> listar(
            @RequestParam(required = false) UUID registroVacunacionId,
            @RequestParam(required = false) UUID estadoId,
            @RequestParam(required = false) CanalNotificacion canal,
            Pageable pageable
    ) {
        return ResponseEntity.ok(notificacionService.listar(registroVacunacionId, estadoId, canal, pageable));
    }

    @GetMapping("/cola")
    @Operation(summary = "Listar cola de notificaciones pendientes listas para transmitir")
    @ApiResponse(responseCode = "200", description = "Cola de notificaciones obtenida exitosamente")
    public ResponseEntity<List<NotificacionDetalleResponse>> listarCola() {
        return ResponseEntity.ok(notificacionService.listarCola());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una notificación por su ID")
    @ApiResponse(responseCode = "200", description = "Notificación obtenida exitosamente")
    @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    public ResponseEntity<NotificacionDetalleResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(notificacionService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear o programar manualmente una notificación")
    @ApiResponse(responseCode = "201", description = "Notificación creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Registro de vacunación no encontrado")
    public ResponseEntity<NotificacionDetalleResponse> crear(@Valid @RequestBody CrearNotificacionRequest request) {
        NotificacionDetalleResponse respuesta = notificacionService.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(respuesta.id())
                .toUri();
        return ResponseEntity.created(location).body(respuesta);
    }

    @RequestMapping(value = "/{id}/enviada", method = {RequestMethod.POST, RequestMethod.PATCH})
    @Operation(summary = "Marcar una notificación como transmitida/enviada exitosamente")
    @ApiResponse(responseCode = "200", description = "Notificación actualizada a estado ENVIADO")
    @ApiResponse(responseCode = "404", description = "Notificación o estado ENVIADO no encontrado")
    public ResponseEntity<NotificacionDetalleResponse> marcarEnviada(
            @PathVariable UUID id,
            @RequestBody(required = false) MarcarEnviadaRequest request
    ) {
        return ResponseEntity.ok(notificacionService.marcarEnviada(id, request));
    }
}
