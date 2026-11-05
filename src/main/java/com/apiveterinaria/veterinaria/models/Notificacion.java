package com.apiveterinaria.veterinaria.models;

import com.apiveterinaria.veterinaria.enums.CanalNotificacion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "notificaciones")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registro_vacunacion_id", nullable = false)
    private RegistroVacunacion registroVacunacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoNotificacion estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal", nullable = false, length = 30)
    private CanalNotificacion canal;

    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "clave_unica", length = 150, unique = true)
    private String claveUnica;

    @Column(name = "id_mensaje_proveedor", length = 150)
    private String idMensajeProveedor;

    @Column(name = "intentos", nullable = false)
    private Integer intentos;

    @Column(name = "enviar_en")
    private OffsetDateTime enviarEn;

    @Column(name = "fecha_envio")
    private OffsetDateTime fechaEnvio;

    @Column(name = "proximo_reintento")
    private OffsetDateTime proximoReintento;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notificacion that = (Notificacion) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
