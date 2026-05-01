package com.diarioclasse.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AtualizarNotaRequest(
        @NotNull @DecimalMin("0.0") @DecimalMax("10.0") BigDecimal notaFinal
) {}
