package com.apiveterinaria.veterinaria.models;

import com.apiveterinaria.veterinaria.enums.TipoMovimiento;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "movimientos_inventario")
@Immutable
@DynamicInsert
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_vacuna_id", nullable = false, updatable = false)
    private LoteVacuna lote;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 30, updatable = false)
    private TipoMovimiento tipoMovimiento;

    @Column(name = "cantidad", nullable = false, precision = 10, scale = 2, updatable = false)
    private BigDecimal cantidad;

    @Column(name = "fecha_movimiento", insertable = false, updatable = false)
    private OffsetDateTime fechaMovimiento;

    @Column(name = "registro_vacunacion_id", updatable = false)
    private UUID registroVacunacionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;

    @Column(name = "observaciones", updatable = false)
    private String observaciones;

    @Column(name = "id_operacion", length = 100, updatable = false)
    private String idOperacion;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovimientoInventario that = (MovimientoInventario) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
