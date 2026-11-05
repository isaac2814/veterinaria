package com.apiveterinaria.veterinaria.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "registro_historico")
@Immutable
@DynamicInsert
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_actor_id", updatable = false)
    private Usuario usuarioActor;

    @Column(nullable = false, length = 50, updatable = false)
    private String accion;

    @Column(nullable = false, length = 100, updatable = false)
    private String entidad;

    @Column(name = "entidad_id", nullable = false, updatable = false)
    private UUID entidadId;

    @Column(name = "fecha_evento", nullable = false, updatable = false)
    private OffsetDateTime fechaEvento;

    @Column(columnDefinition = "jsonb", updatable = false)
    private String cambios;

    @Column(length = 50, updatable = false)
    private String resultado;

    @Column(name = "id_operacion", length = 100, updatable = false)
    private String idOperacion;

    @Column(length = 100, updatable = false)
    private String origen;

    @Column(name = "ip_origen", length = 45, updatable = false)
    private String ipOrigen;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistroHistorico that = (RegistroHistorico) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
