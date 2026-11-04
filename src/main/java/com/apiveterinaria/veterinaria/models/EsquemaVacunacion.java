package com.apiveterinaria.veterinaria.models;

import com.apiveterinaria.veterinaria.enums.UnidadTiempo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "esquemas_vacunacion", uniqueConstraints = {
        @UniqueConstraint(name = "esquemas_vacunacion_vacuna_id_especie_id_nombre_version_key",
                columnNames = {"vacuna_id", "especie_id", "nombre", "version"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class EsquemaVacunacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacuna_id", nullable = false)
    private Vacuna vacuna;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especie_id", nullable = false)
    private Especie especie;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "edad_inicial_valor")
    private Integer edadInicialValor;

    @Enumerated(EnumType.STRING)
    @Column(name = "edad_inicial_unidad", length = 20)
    private UnidadTiempo edadInicialUnidad;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "version", insertable = false, updatable = false)
    private Integer version;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EsquemaVacunacion that = (EsquemaVacunacion) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
