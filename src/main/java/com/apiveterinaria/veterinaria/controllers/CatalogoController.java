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

    // ==========================================
    // ROLES
    // ==========================================

    @GetMapping("/roles")
    @Operation(summary = "Listar roles del sistema")
    @ApiResponse(responseCode = "200", description = "Listado de roles obtenido exitosamente")
    public ResponseEntity<List<RolResponse>> listarRoles() {
        return ResponseEntity.ok(catalogoService.listarRoles());
    }

    @GetMapping("/roles/{id}")
    @Operation(summary = "Obtener un rol por su ID")
    @ApiResponse(responseCode = "200", description = "Rol obtenido exitosamente")
    @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    public ResponseEntity<RolResponse> obtenerRolPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(catalogoService.obtenerRolPorId(id));
    }

    @PostMapping("/roles")
    @Operation(summary = "Crear un nuevo rol en el sistema")
    @ApiResponse(responseCode = "201", description = "Rol creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "Nombre o código de rol ya existente")
    public ResponseEntity<RolResponse> crearRol(@Valid @RequestBody CrearRolRequest request) {
        RolResponse respuesta = catalogoService.crearRol(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(respuesta.id())
                .toUri();
        return ResponseEntity.created(location).body(respuesta);
    }

    // ==========================================
    // ESPECIES
    // ==========================================

    @GetMapping("/especies")
    @Operation(summary = "Listar especies animales registradas")
    @ApiResponse(responseCode = "200", description = "Listado de especies obtenido exitosamente")
    public ResponseEntity<List<EspecieResponse>> listarEspecies() {
        return ResponseEntity.ok(catalogoService.listarEspecies());
    }

    @GetMapping("/especies/{id}")
    @Operation(summary = "Obtener una especie por su ID")
    @ApiResponse(responseCode = "200", description = "Especie obtenida exitosamente")
    @ApiResponse(responseCode = "404", description = "Especie no encontrada")
    public ResponseEntity<EspecieResponse> obtenerEspeciePorId(@PathVariable UUID id) {
        return ResponseEntity.ok(catalogoService.obtenerEspeciePorId(id));
    }

    @PostMapping("/especies")
    @Operation(summary = "Crear una nueva especie animal")
    @ApiResponse(responseCode = "201", description = "Especie creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "Nombre o código de especie ya existente")
    public ResponseEntity<EspecieResponse> crearEspecie(@Valid @RequestBody CrearEspecieRequest request) {
        EspecieResponse respuesta = catalogoService.crearEspecie(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(respuesta.id())
                .toUri();
        return ResponseEntity.created(location).body(respuesta);
    }

    // ==========================================
    // RAZAS
    // ==========================================

    @GetMapping("/razas")
    @Operation(summary = "Listar razas, opcionalmente filtradas por ID de especie")
    @ApiResponse(responseCode = "200", description = "Listado de razas obtenido exitosamente")
    public ResponseEntity<List<RazaResponse>> listarRazas(@RequestParam(required = false) UUID especieId) {
        return ResponseEntity.ok(catalogoService.listarRazas(especieId));
    }

    @GetMapping("/razas/{id}")
    @Operation(summary = "Obtener una raza por su ID")
    @ApiResponse(responseCode = "200", description = "Raza obtenida exitosamente")
    @ApiResponse(responseCode = "404", description = "Raza no encontrada")
    public ResponseEntity<RazaResponse> obtenerRazaPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(catalogoService.obtenerRazaPorId(id));
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

    // ==========================================
    // ESTADOS NOTIFICACIÓN
    // ==========================================

    @GetMapping("/estados-notificacion")
    @Operation(summary = "Listar estados de notificación posibles")
    @ApiResponse(responseCode = "200", description = "Listado de estados de notificación obtenido exitosamente")
    public ResponseEntity<List<EstadoNotificacionResponse>> listarEstadosNotificacion() {
        return ResponseEntity.ok(catalogoService.listarEstadosNotificacion());
    }

    @GetMapping("/estados-notificacion/{id}")
    @Operation(summary = "Obtener un estado de notificación por su ID")
    @ApiResponse(responseCode = "200", description = "Estado de notificación obtenido exitosamente")
    @ApiResponse(responseCode = "404", description = "Estado de notificación no encontrado")
    public ResponseEntity<EstadoNotificacionResponse> obtenerEstadoNotificacionPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(catalogoService.obtenerEstadoNotificacionPorId(id));
    }

    @PostMapping("/estados-notificacion")
    @Operation(summary = "Crear un nuevo estado de notificación")
    @ApiResponse(responseCode = "201", description = "Estado de notificación creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "Nombre o código de estado ya existente")
    public ResponseEntity<EstadoNotificacionResponse> crearEstadoNotificacion(@Valid @RequestBody CrearEstadoNotificacionRequest request) {
        EstadoNotificacionResponse respuesta = catalogoService.crearEstadoNotificacion(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(respuesta.id())
                .toUri();
        return ResponseEntity.created(location).body(respuesta);
    }
}
