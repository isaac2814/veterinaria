package com.apiveterinaria.veterinaria.controllers;

import com.apiveterinaria.veterinaria.dto.ActualizarPropietarioRequest;
import com.apiveterinaria.veterinaria.dto.CrearPropietarioRequest;
import com.apiveterinaria.veterinaria.dto.PropietarioResponse;
import com.apiveterinaria.veterinaria.services.PropietarioService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/propietarios")
@Tag(name = "Propietarios", description = "Endpoints para la gestión de propietarios de mascotas")
public class PropietarioController {

    private final PropietarioService propietarioService;

    public PropietarioController(PropietarioService propietarioService) {
        this.propietarioService = propietarioService;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo propietario de mascotas")
    @ApiResponse(responseCode = "201", description = "Propietario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "El documento ya está registrado")
    public ResponseEntity<PropietarioResponse> crear(@Valid @RequestBody CrearPropietarioRequest request) {
        PropietarioResponse respuesta = propietarioService.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(respuesta.id())
                .toUri();
        return ResponseEntity.created(location).body(respuesta);
    }

    @GetMapping
    @Operation(summary = "Listar propietarios con filtros y paginación")
    @ApiResponse(responseCode = "200", description = "Página de propietarios obtenida exitosamente")
    public ResponseEntity<Page<PropietarioResponse>> listar(
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) String q,
            Pageable pageable
    ) {
        return ResponseEntity.ok(propietarioService.listar(activo, q, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un propietario por su ID")
    @ApiResponse(responseCode = "200", description = "Propietario obtenido exitosamente")
    @ApiResponse(responseCode = "404", description = "Propietario no encontrado")
    public ResponseEntity<PropietarioResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(propietarioService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un propietario")
    @ApiResponse(responseCode = "200", description = "Propietario actualizado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Propietario no encontrado")
    @ApiResponse(responseCode = "409", description = "El nuevo documento ya está en uso")
    public ResponseEntity<PropietarioResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarPropietarioRequest request
    ) {
        return ResponseEntity.ok(propietarioService.actualizar(id, request));
    }

    @PatchMapping("/{id}/consentimiento-whatsapp")
    @Operation(summary = "Actualizar el consentimiento de notificaciones por WhatsApp")
    @ApiResponse(responseCode = "200", description = "Consentimiento de WhatsApp actualizado")
    @ApiResponse(responseCode = "404", description = "Propietario no encontrado")
    public ResponseEntity<PropietarioResponse> cambiarConsentimientoWhatsapp(
            @PathVariable UUID id,
            @RequestParam boolean valor
    ) {
        return ResponseEntity.ok(propietarioService.cambiarConsentimientoWhatsapp(id, valor));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar un propietario (baja lógica)")
    @ApiResponse(responseCode = "204", description = "Propietario desactivado exitosamente")
    @ApiResponse(responseCode = "404", description = "Propietario no encontrado")
    public ResponseEntity<Void> desactivar(@PathVariable UUID id) {
        propietarioService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
