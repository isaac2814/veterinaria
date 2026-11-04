package com.apiveterinaria.veterinaria.controllers;

import com.apiveterinaria.veterinaria.dto.ActualizarMascotaRequest;
import com.apiveterinaria.veterinaria.dto.CrearMascotaRequest;
import com.apiveterinaria.veterinaria.dto.MascotaResponse;
import com.apiveterinaria.veterinaria.enums.EstadoMascota;
import com.apiveterinaria.veterinaria.services.MascotaService;
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
@RequestMapping("/api/mascotas")
@Tag(name = "Mascotas", description = "Endpoints para la gestión de mascotas y pacientes")
public class MascotaController {

    private final MascotaService mascotaService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @PostMapping
    @Operation(summary = "Registrar una nueva mascota")
    @ApiResponse(responseCode = "201", description = "Mascota registrada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o fecha futura")
    @ApiResponse(responseCode = "404", description = "Propietario o raza no encontrada")
    @ApiResponse(responseCode = "422", description = "El propietario no está activo o la fecha de nacimiento es inválida")
    public ResponseEntity<MascotaResponse> crear(@Valid @RequestBody CrearMascotaRequest request) {
        MascotaResponse respuesta = mascotaService.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(respuesta.id())
                .toUri();
        return ResponseEntity.created(location).body(respuesta);
    }

    @GetMapping
    @Operation(summary = "Listar mascotas con filtros y paginación")
    @ApiResponse(responseCode = "200", description = "Página de mascotas obtenida exitosamente")
    public ResponseEntity<Page<MascotaResponse>> listar(
            @RequestParam(required = false) UUID propietarioId,
            @RequestParam(required = false) UUID especieId,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) EstadoMascota estado,
            Pageable pageable
    ) {
        return ResponseEntity.ok(mascotaService.listar(propietarioId, especieId, nombre, estado, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una mascota por su ID")
    @ApiResponse(responseCode = "200", description = "Mascota obtenida exitosamente")
    @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    public ResponseEntity<MascotaResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(mascotaService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de una mascota")
    @ApiResponse(responseCode = "200", description = "Mascota actualizada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Mascota o raza no encontrada")
    @ApiResponse(responseCode = "422", description = "Fecha de nacimiento futura")
    public ResponseEntity<MascotaResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarMascotaRequest request
    ) {
        return ResponseEntity.ok(mascotaService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado de una mascota")
    @ApiResponse(responseCode = "200", description = "Estado de la mascota actualizado exitosamente")
    @ApiResponse(responseCode = "400", description = "Valor de estado inválido")
    @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    public ResponseEntity<MascotaResponse> cambiarEstado(
            @PathVariable UUID id,
            @RequestParam EstadoMascota valor
    ) {
        return ResponseEntity.ok(mascotaService.cambiarEstado(id, valor));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inactivar una mascota (pasa a estado INACTIVO)")
    @ApiResponse(responseCode = "204", description = "Mascota inactivada exitosamente")
    @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    public ResponseEntity<Void> desactivar(@PathVariable UUID id) {
        mascotaService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
