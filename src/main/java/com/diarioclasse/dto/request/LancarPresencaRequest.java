package com.diarioclasse.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.List;

public record LancarPresencaRequest(
        @NotNull(message = "idMateria é obrigatório")
        Integer idMateria,

        @NotNull(message = "data é obrigatória")
        @PastOrPresent(message = "não é possível registrar presença em data futura")
        LocalDate data,

        @NotEmpty(message = "a lista de presenças não pode ser vazia")
        @Valid
        List<ItemPresencaRequest> presencas
) {}
