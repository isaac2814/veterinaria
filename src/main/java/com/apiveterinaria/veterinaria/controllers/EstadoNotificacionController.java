package com.apiveterinaria.veterinaria.controllers;

import com.apiveterinaria.veterinaria.dto.ActualizarEstadoNotificacionRequest;
import com.apiveterinaria.veterinaria.dto.CrearEstadoNotificacionRequest;
import com.apiveterinaria.veterinaria.dto.EstadoNotificacionResponse;
import com.apiveterinaria.veterinaria.services.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/estados-notificacion")
@Tag(name = "Estados de Notificación", description = "Endpoints para la gestión y CRUD de estados de notificación")
public class EstadoNotificacionController {

    private final CatalogoService catalogoService;

    public EstadoNotificacionController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping
    @Operation(summary = "Listar estados de notificación posibles")
    @ApiResponse(responseCode = "200", description = "Listado de estados de notificación obtenido exitosamente")
    public ResponseEntity<List<EstadoNotificacionResponse>> listar() {
        return ResponseEntity.ok(catalogoService.listarEstadosNotificacion());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un estado de notificación por su ID")
    @ApiResponse(responseCode = "200", description = "Estado de notificación obtenido exitosamente")
    @ApiResponse(responseCode = "404", description = "Estado de notificación no encontrado")
    public ResponseEntity<EstadoNotificacionResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(catalogoService.obtenerEstadoNotificacionPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo estado de notificación")
    @ApiResponse(responseCode = "201", description = "Estado de notificación creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "Nombre o código de estado ya existente")
    public ResponseEntity<EstadoNotificacionResponse> crear(@Valid @RequestBody CrearEstadoNotificacionRequest request) {
        EstadoNotificacionResponse respuesta = catalogoService.crearEstadoNotificacion(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(respuesta.id())
                .toUri();
        return ResponseEntity.created(location).body(respuesta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un estado de notificación")
    @ApiResponse(responseCode = "200", description = "Estado de notificación actualizado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Estado de notificación no encontrado")
    @ApiResponse(responseCode = "409", description = "Ya existe otro estado con ese nombre")
    public ResponseEntity<EstadoNotificacionResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarEstadoNotificacionRequest request
    ) {
        return ResponseEntity.ok(catalogoService.actualizarEstadoNotificacion(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un estado de notificación")
    @ApiResponse(responseCode = "204", description = "Estado de notificación eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Estado de notificación no encontrado")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        catalogoService.eliminarEstadoNotificacion(id);
        return ResponseEntity.noContent().build();
    }
}
