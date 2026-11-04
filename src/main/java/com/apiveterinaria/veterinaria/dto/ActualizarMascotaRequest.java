package com.apiveterinaria.veterinaria.dto;

import com.apiveterinaria.veterinaria.enums.Sexo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ActualizarMascotaRequest(
        @NotBlank(message = "El nombre de la mascota es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        @Schema(example = "Maximiliano")
        String nombre,

        @NotNull(message = "La raza es obligatoria")
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa7")
        UUID razaId,

        @NotNull(message = "El sexo es obligatorio")
        @Schema(example = "MACHO")
        Sexo sexo,

        @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
        @Schema(example = "2023-05-15")
        LocalDate fechaNacimiento,

        @Positive(message = "El peso debe ser mayor a 0")
        @Digits(integer = 4, fraction = 2, message = "El peso debe tener máximo 4 enteros y 2 decimales")
        @Schema(example = "13.20")
        BigDecimal pesoKg
) {
}
