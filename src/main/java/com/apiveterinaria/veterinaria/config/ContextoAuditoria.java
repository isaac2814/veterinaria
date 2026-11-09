package com.apiveterinaria.veterinaria.config;

import com.apiveterinaria.veterinaria.repositories.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Component
public class ContextoAuditoria {

    @PersistenceContext
    private final EntityManager entityManager;

    private final UsuarioRepository usuarioRepository;

    public ContextoAuditoria(EntityManager entityManager, @Lazy UsuarioRepository usuarioRepository) {
        this.entityManager = entityManager;
        this.usuarioRepository = usuarioRepository;
    }

    public void aplicar() {
        String usuarioId = "";
        String ipOrigen = "127.0.0.1";

        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            HttpServletRequest request = servletAttributes.getRequest();
            String headerUsuario = request.getHeader("X-Usuario-Id");
            if (headerUsuario != null && !headerUsuario.isBlank()) {
                try {
                    UUID parsedUuid = UUID.fromString(headerUsuario.trim());
                    if (usuarioRepository.existsById(parsedUuid)) {
                        usuarioId = parsedUuid.toString();
                    }
                } catch (IllegalArgumentException ignored) {
                    // Si el header contiene texto no-UUID (como 'string' de Swagger), se ignora de forma segura
                }
            }

            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isBlank()) {
                ipOrigen = xForwardedFor.split(",")[0].trim();
            } else if (request.getRemoteAddr() != null && !request.getRemoteAddr().isBlank()) {
                ipOrigen = request.getRemoteAddr();
            }
        }

        entityManager.createNativeQuery(
                "SELECT set_config('app.usuario_id', :usuario, true), " +
                "       set_config('app.origen', 'API', true), " +
                "       set_config('app.ip_origen', :ip, true)"
        )
        .setParameter("usuario", usuarioId)
        .setParameter("ip", ipOrigen)
        .getSingleResult();
    }
}
