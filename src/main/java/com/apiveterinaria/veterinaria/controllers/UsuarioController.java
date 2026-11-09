package com.apiveterinaria.veterinaria.controllers;

import com.apiveterinaria.veterinaria.dto.ActualizarUsuarioRequest;
import com.apiveterinaria.veterinaria.dto.CambiarPasswordRequest;
import com.apiveterinaria.veterinaria.dto.CrearUsuarioRequest;
import com.apiveterinaria.veterinaria.dto.UsuarioResponse;
import com.apiveterinaria.veterinaria.services.UsuarioService;
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
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Endpoints para la gestión de usuarios y personal de la clínica")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo usuario")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o contraseña menor a 8 caracteres")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    @ApiResponse(responseCode = "409", description = "El email ya está registrado")
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody CrearUsuarioRequest request) {
        UsuarioResponse respuesta = usuarioService.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(respuesta.id())
                .toUri();
        return ResponseEntity.created(location).body(respuesta);
    }

    @GetMapping
    @Operation(summary = "Listar usuarios con filtros y paginación")
    @ApiResponse(responseCode = "200", description = "Página de usuarios obtenida exitosamente")
    public ResponseEntity<Page<UsuarioResponse>> listar(
            @RequestParam(required = false) UUID rolId,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) String q,
            Pageable pageable
    ) {
        return ResponseEntity.ok(usuarioService.listar(rolId, activo, q, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por su ID")
    @ApiResponse(responseCode = "200", description = "Usuario obtenido exitosamente")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un usuario (sin contraseña)")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Usuario o rol no encontrado")
    @ApiResponse(responseCode = "409", description = "El nuevo email ya está en uso")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarUsuarioRequest request
    ) {
        return ResponseEntity.ok(usuarioService.actualizar(id, request));
    }

    @PatchMapping("/{id}/activo")
    @Operation(summary = "Activar o desactivar un usuario")
    @ApiResponse(responseCode = "200", description = "Estado activo del usuario modificado exitosamente")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<UsuarioResponse> cambiarEstadoActivo(
            @PathVariable UUID id,
            @RequestParam boolean valor
    ) {
        return ResponseEntity.ok(usuarioService.cambiarEstadoActivo(id, valor));
    }

    @PatchMapping("/{id}/password")
    @Operation(summary = "Cambiar la contraseña de un usuario")
    @ApiResponse(responseCode = "204", description = "Contraseña actualizada exitosamente")
    @ApiResponse(responseCode = "400", description = "Contraseña inválida o menor a 8 caracteres")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<Void> cambiarPassword(
            @PathVariable UUID id,
            @Valid @RequestBody CambiarPasswordRequest request
    ) {
        usuarioService.cambiarPassword(id, request.passwordNueva());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
