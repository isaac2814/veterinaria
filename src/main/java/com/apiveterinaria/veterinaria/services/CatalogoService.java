package com.apiveterinaria.veterinaria.services;

import com.apiveterinaria.veterinaria.config.ContextoAuditoria;
import com.apiveterinaria.veterinaria.dto.*;
import com.apiveterinaria.veterinaria.exception.RecursoNoEncontradoException;
import com.apiveterinaria.veterinaria.models.Especie;
import com.apiveterinaria.veterinaria.models.EstadoNotificacion;
import com.apiveterinaria.veterinaria.models.Raza;
import com.apiveterinaria.veterinaria.models.Rol;
import com.apiveterinaria.veterinaria.repositories.*;
import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CatalogoService {

    private final RolRepository rolRepository;
    private final EspecieRepository especieRepository;
    private final RazaRepository razaRepository;
    private final EstadoNotificacionRepository estadoNotificacionRepository;
    private final MascotaRepository mascotaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContextoAuditoria contextoAuditoria;
    private final EntityManager entityManager;

    public CatalogoService(RolRepository rolRepository,
                           EspecieRepository especieRepository,
                           RazaRepository razaRepository,
                           EstadoNotificacionRepository estadoNotificacionRepository,
                           MascotaRepository mascotaRepository,
                           UsuarioRepository usuarioRepository,
                           ContextoAuditoria contextoAuditoria,
                           EntityManager entityManager) {
        this.rolRepository = rolRepository;
        this.especieRepository = especieRepository;
        this.razaRepository = razaRepository;
        this.estadoNotificacionRepository = estadoNotificacionRepository;
        this.mascotaRepository = mascotaRepository;
        this.usuarioRepository = usuarioRepository;
        this.contextoAuditoria = contextoAuditoria;
        this.entityManager = entityManager;
    }

    // ==========================================
    // ROLES
    // ==========================================

    public List<RolResponse> listarRoles() {
        return rolRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(RolResponse::from)
                .toList();
    }

    public RolResponse obtenerRolPorId(UUID id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con ID: " + id));
        return RolResponse.from(rol);
    }

    @Transactional
    public RolResponse crearRol(CrearRolRequest request) {
        contextoAuditoria.aplicar();

        String nombre = request.nombre().trim();
        String codigo = (request.codigo() != null && !request.codigo().isBlank())
                ? request.codigo().trim().toUpperCase()
                : nombre.toUpperCase().replaceAll("[^A-Z0-9_]+", "_");

        if (rolRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un rol con el nombre '" + nombre + "'");
        }
        if (rolRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un rol con el código '" + codigo + "'");
        }

        Rol rol = Rol.builder()
                .nombre(nombre)
                .codigo(codigo)
                .descripcion(request.descripcion() != null ? request.descripcion().trim() : null)
                .build();

        rol = rolRepository.save(rol);
        entityManager.flush();
        entityManager.refresh(rol);

        return RolResponse.from(rol);
    }

    @Transactional
    public RolResponse actualizarRol(UUID id, ActualizarRolRequest request) {
        contextoAuditoria.aplicar();

        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con ID: " + id));

        String nombre = request.nombre().trim();
        if (rolRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otro rol con el nombre '" + nombre + "'");
        }

        rol.setNombre(nombre);
        if (request.descripcion() != null) {
            rol.setDescripcion(request.descripcion().trim());
        }

        rol = rolRepository.save(rol);
        entityManager.flush();
        entityManager.refresh(rol);

        return RolResponse.from(rol);
    }

    @Transactional
    public void eliminarRol(UUID id) {
        contextoAuditoria.aplicar();

        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con ID: " + id));

        if (usuarioRepository.existsByRolId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar el rol porque existen usuarios asociados a él");
        }

        rolRepository.delete(rol);
        entityManager.flush();
    }

    // ==========================================
    // ESPECIES
    // ==========================================

    public List<EspecieResponse> listarEspecies() {
        return especieRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(EspecieResponse::from)
                .toList();
    }

    public EspecieResponse obtenerEspeciePorId(UUID id) {
        Especie especie = especieRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Especie no encontrada con ID: " + id));
        return EspecieResponse.from(especie);
    }

    @Transactional
    public EspecieResponse crearEspecie(CrearEspecieRequest request) {
        contextoAuditoria.aplicar();

        String nombre = request.nombre().trim();
        String codigo = (request.codigo() != null && !request.codigo().isBlank())
                ? request.codigo().trim().toUpperCase()
                : (nombre.length() >= 3 ? nombre.substring(0, 3).toUpperCase() : nombre.toUpperCase());

        if (especieRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una especie con el nombre '" + nombre + "'");
        }
        if (especieRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una especie con el código '" + codigo + "'");
        }

        Especie especie = Especie.builder()
                .nombre(nombre)
                .codigo(codigo)
                .descripcion(request.descripcion() != null ? request.descripcion().trim() : null)
                .build();

        especie = especieRepository.save(especie);
        entityManager.flush();
        entityManager.refresh(especie);

        return EspecieResponse.from(especie);
    }

    @Transactional
    public EspecieResponse actualizarEspecie(UUID id, ActualizarEspecieRequest request) {
        contextoAuditoria.aplicar();

        Especie especie = especieRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Especie no encontrada con ID: " + id));

        String nombre = request.nombre().trim();
        if (especieRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otra especie con el nombre '" + nombre + "'");
        }

        especie.setNombre(nombre);
        if (request.descripcion() != null) {
            especie.setDescripcion(request.descripcion().trim());
        }

        especie = especieRepository.save(especie);
        entityManager.flush();
        entityManager.refresh(especie);

        return EspecieResponse.from(especie);
    }

    @Transactional
    public void eliminarEspecie(UUID id) {
        contextoAuditoria.aplicar();

        Especie especie = especieRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Especie no encontrada con ID: " + id));

        if (razaRepository.existsByEspecieId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar la especie porque tiene razas asociadas");
        }
        if (mascotaRepository.existsByRazaEspecieId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar la especie porque tiene mascotas asociadas");
        }

        especieRepository.delete(especie);
        entityManager.flush();
    }

    // ==========================================
    // RAZAS
    // ==========================================

    public List<RazaResponse> listarRazas(UUID especieId) {
        List<Raza> razas = (especieId != null)
                ? razaRepository.findByEspecieIdOrderByNombreAsc(especieId)
                : razaRepository.findAllByOrderByNombreAsc();

        return razas.stream()
                .map(RazaResponse::from)
                .toList();
    }

    public RazaResponse obtenerRazaPorId(UUID id) {
        Raza raza = razaRepository.findWithEspecieById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Raza no encontrada con ID: " + id));
        return RazaResponse.from(raza);
    }

    @Transactional
    public RazaResponse crearRaza(CrearRazaRequest request) {
        contextoAuditoria.aplicar();

        Especie especie = null;
        if (request.especieId() != null) {
            especie = especieRepository.findById(request.especieId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Especie no encontrada con ID: " + request.especieId()));
        } else if (request.nombreEspecie() != null && !request.nombreEspecie().isBlank()) {
            String nombreEsp = request.nombreEspecie().trim();
            especie = especieRepository.findByNombreIgnoreCase(nombreEsp)
                    .orElseGet(() -> {
                        String codigo = nombreEsp.length() >= 3 ? nombreEsp.substring(0, 3).toUpperCase() : nombreEsp.toUpperCase();
                        Especie nueva = Especie.builder()
                                .nombre(nombreEsp)
                                .codigo(codigo)
                                .build();
                        nueva = especieRepository.save(nueva);
                        entityManager.flush();
                        return nueva;
                    });
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe especificar el ID de la especie (especieId) o su nombre (nombreEspecie)");
        }

        String nombre = request.nombre().trim();
        if (razaRepository.existsByEspecieIdAndNombreIgnoreCase(especie.getId(), nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe una raza con el nombre '" + nombre + "' para la especie '" + especie.getNombre() + "'");
        }

        Raza raza = Raza.builder()
                .especie(especie)
                .nombre(nombre)
                .descripcion(request.descripcion() != null ? request.descripcion().trim() : null)
                .build();

        raza = razaRepository.save(raza);
        entityManager.flush();
        entityManager.refresh(raza);

        return RazaResponse.from(raza);
    }

    @Transactional
    public RazaResponse actualizarRaza(UUID id, ActualizarRazaRequest request) {
        contextoAuditoria.aplicar();

        Raza raza = razaRepository.findWithEspecieById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Raza no encontrada con ID: " + id));

        if (request.especieId() != null && !request.especieId().equals(raza.getEspecie().getId())) {
            Especie nuevaEspecie = especieRepository.findById(request.especieId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Especie no encontrada con ID: " + request.especieId()));
            raza.setEspecie(nuevaEspecie);
        }

        String nombre = request.nombre().trim();
        if (razaRepository.existsByEspecieIdAndNombreIgnoreCaseAndIdNot(raza.getEspecie().getId(), nombre, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe otra raza con el nombre '" + nombre + "' para la especie '" + raza.getEspecie().getNombre() + "'");
        }

        raza.setNombre(nombre);
        if (request.descripcion() != null) {
            raza.setDescripcion(request.descripcion().trim());
        }

        raza = razaRepository.save(raza);
        entityManager.flush();
        entityManager.refresh(raza);

        return RazaResponse.from(raza);
    }

    @Transactional
    public void eliminarRaza(UUID id) {
        contextoAuditoria.aplicar();

        Raza raza = razaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Raza no encontrada con ID: " + id));

        if (mascotaRepository.existsByRazaId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar la raza porque tiene mascotas asociadas");
        }

        razaRepository.delete(raza);
        entityManager.flush();
    }

    // ==========================================
    // ESTADOS NOTIFICACIÓN
    // ==========================================

    public List<EstadoNotificacionResponse> listarEstadosNotificacion() {
        return estadoNotificacionRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(EstadoNotificacionResponse::from)
                .toList();
    }

    public EstadoNotificacionResponse obtenerEstadoNotificacionPorId(UUID id) {
        EstadoNotificacion estado = estadoNotificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estado de notificación no encontrado con ID: " + id));
        return EstadoNotificacionResponse.from(estado);
    }

    @Transactional
    public EstadoNotificacionResponse crearEstadoNotificacion(CrearEstadoNotificacionRequest request) {
        contextoAuditoria.aplicar();

        String nombre = request.nombre().trim();
        String codigo = (request.codigo() != null && !request.codigo().isBlank())
                ? request.codigo().trim().toUpperCase()
                : nombre.toUpperCase().replaceAll("[^A-Z0-9_]+", "_");

        if (estadoNotificacionRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un estado con el nombre '" + nombre + "'");
        }
        if (estadoNotificacionRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un estado con el código '" + codigo + "'");
        }

        EstadoNotificacion estado = EstadoNotificacion.builder()
                .nombre(nombre)
                .codigo(codigo)
                .descripcion(request.descripcion() != null ? request.descripcion().trim() : null)
                .build();

        estado = estadoNotificacionRepository.save(estado);
        entityManager.flush();
        entityManager.refresh(estado);

        return EstadoNotificacionResponse.from(estado);
    }

    @Transactional
    public EstadoNotificacionResponse actualizarEstadoNotificacion(UUID id, ActualizarEstadoNotificacionRequest request) {
        contextoAuditoria.aplicar();

        EstadoNotificacion estado = estadoNotificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estado de notificación no encontrado con ID: " + id));

        String nombre = request.nombre().trim();
        if (estadoNotificacionRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otro estado con el nombre '" + nombre + "'");
        }

        estado.setNombre(nombre);
        if (request.descripcion() != null) {
            estado.setDescripcion(request.descripcion().trim());
        }

        estado = estadoNotificacionRepository.save(estado);
        entityManager.flush();
        entityManager.refresh(estado);

        return EstadoNotificacionResponse.from(estado);
    }

    @Transactional
    public void eliminarEstadoNotificacion(UUID id) {
        contextoAuditoria.aplicar();

        EstadoNotificacion estado = estadoNotificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estado de notificación no encontrado con ID: " + id));

        estadoNotificacionRepository.delete(estado);
        entityManager.flush();
    }
}
