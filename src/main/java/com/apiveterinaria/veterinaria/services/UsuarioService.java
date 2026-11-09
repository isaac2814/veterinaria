package com.apiveterinaria.veterinaria.services;

import com.apiveterinaria.veterinaria.config.ContextoAuditoria;
import com.apiveterinaria.veterinaria.config.PaginacionUtil;
import com.apiveterinaria.veterinaria.dto.ActualizarUsuarioRequest;
import com.apiveterinaria.veterinaria.dto.CrearUsuarioRequest;
import com.apiveterinaria.veterinaria.dto.UsuarioResponse;
import com.apiveterinaria.veterinaria.exception.RecursoNoEncontradoException;
import com.apiveterinaria.veterinaria.models.Rol;
import com.apiveterinaria.veterinaria.models.Usuario;
import com.apiveterinaria.veterinaria.repositories.RolRepository;
import com.apiveterinaria.veterinaria.repositories.UsuarioRepository;

import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private static final Set<String> CAMPOS_ORDEN_PERMITIDOS = Set.of(
            "id", "nombre", "apellidos", "email", "activo", "ultimoAcceso", "createdAt", "updatedAt"
    );

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final ContextoAuditoria contextoAuditoria;
    private final EntityManager entityManager;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder,
                          ContextoAuditoria contextoAuditoria,
                          EntityManager entityManager) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.contextoAuditoria = contextoAuditoria;
        this.entityManager = entityManager;
    }

    @Transactional
    public UsuarioResponse crear(CrearUsuarioRequest request) {
        contextoAuditoria.aplicar();

        Rol rol = rolRepository.findById(request.rolId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con ID: " + request.rolId()));

        String emailNormalizado = request.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmailIgnoreCase(emailNormalizado)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "El email '" + emailNormalizado + "' ya se encuentra registrado");
        }
        String passwordHasheada = passwordEncoder.encode(request.password());

        Usuario usuario = Usuario.builder()
                .nombre(request.nombre().trim())
                .apellidos(request.apellidos().trim())
                .email(emailNormalizado)
                .passwordHash(passwordHasheada)
                .rol(rol)
                .activo(true)
                .build();

        usuario = usuarioRepository.save(usuario);
        entityManager.flush();
        entityManager.refresh(usuario);

        return UsuarioResponse.from(usuario);
    }

    public Page<UsuarioResponse> listar(UUID rolId, Boolean activo, String q, Pageable pageable) {
        Pageable pageableValido = PaginacionUtil.validarOrden(pageable, CAMPOS_ORDEN_PERMITIDOS, "createdAt");
        String filtroQ = (q != null && !q.isBlank()) ? "%" + q.trim().toLowerCase() + "%" : null;

        return usuarioRepository.buscarUsuarios(rolId, activo, filtroQ, pageableValido)
                .map(UsuarioResponse::from);
    }

    public UsuarioResponse obtenerPorId(UUID id) {
        Usuario usuario = usuarioRepository.findWithRolById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public UsuarioResponse actualizar(UUID id, ActualizarUsuarioRequest request) {
        contextoAuditoria.aplicar();

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        Rol rol = rolRepository.findById(request.rolId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con ID: " + request.rolId()));

        usuario.setNombre(request.nombre().trim());
        usuario.setApellidos(request.apellidos().trim());
        usuario.setEmail(request.email().trim().toLowerCase());
        usuario.setRol(rol);

        usuario = usuarioRepository.save(usuario);
        entityManager.flush();
        entityManager.refresh(usuario);

        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public UsuarioResponse cambiarEstadoActivo(UUID id, boolean valor) {
        contextoAuditoria.aplicar();

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        usuario.setActivo(valor);
        usuario = usuarioRepository.save(usuario);
        entityManager.flush();
        entityManager.refresh(usuario);

        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public void cambiarPassword(UUID id, String passwordNueva) {
        contextoAuditoria.aplicar();

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        usuario.setPasswordHash(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
        entityManager.flush();
    }

    @Transactional
    public void eliminar(UUID id) {
        contextoAuditoria.aplicar();

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        usuarioRepository.delete(usuario);
        entityManager.flush();
    }
}
