package com.apiveterinaria.veterinaria.controllers;

import com.apiveterinaria.veterinaria.dto.*;
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
@RequestMapping("/api")
@Tag(name = "Catálogos", description = "Endpoints para consulta y gestión de catálogos del sistema")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/roles")
    @Operation(summary = "Listar roles del sistema")
    @ApiResponse(responseCode = "200", description = "Listado de roles obtenido exitosamente")
    public ResponseEntity<List<RolResponse>> listarRoles() {
        return ResponseEntity.ok(catalogoService.listarRoles());
    }

    @GetMapping("/especies")
    @Operation(summary = "Listar especies animales registradas")
    @ApiResponse(responseCode = "200", description = "Listado de especies obtenido exitosamente")
    public ResponseEntity<List<EspecieResponse>> listarEspecies() {
        return ResponseEntity.ok(catalogoService.listarEspecies());
    }

    @GetMapping("/razas")
    @Operation(summary = "Listar razas, opcionalmente filtradas por ID de especie")
    @ApiResponse(responseCode = "200", description = "Listado de razas obtenido exitosamente")
    public ResponseEntity<List<RazaResponse>> listarRazas(@RequestParam(required = false) UUID especieId) {
        return ResponseEntity.ok(catalogoService.listarRazas(especieId));
    }

    @PostMapping("/razas")
    @Operation(summary = "Crear una nueva raza para una especie")
    @ApiResponse(responseCode = "201", description = "Raza creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Especie no encontrada")
    @ApiResponse(responseCode = "409", description = "Raza ya existente para la especie indicada")
    public ResponseEntity<RazaResponse> crearRaza(@Valid @RequestBody CrearRazaRequest request) {
        RazaResponse respuesta = catalogoService.crearRaza(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(respuesta.id())
                .toUri();
        return ResponseEntity.created(location).body(respuesta);
    }

    @GetMapping("/estados-notificacion")
    @Operation(summary = "Listar estados de notificación posibles")
    @ApiResponse(responseCode = "200", description = "Listado de estados de notificación obtenido exitosamente")
    public ResponseEntity<List<EstadoNotificacionResponse>> listarEstadosNotificacion() {
        return ResponseEntity.ok(catalogoService.listarEstadosNotificacion());
    }
}
