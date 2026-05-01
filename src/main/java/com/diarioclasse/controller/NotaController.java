package com.diarioclasse.controller;

import com.diarioclasse.dto.request.AtualizarNotaRequest;
import com.diarioclasse.dto.request.LancarNotaRequest;
import com.diarioclasse.dto.response.BoletimResponse;
import com.diarioclasse.dto.response.ErroResponse;
import com.diarioclasse.dto.response.NotaResponse;
import com.diarioclasse.service.NotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notas")
@Tag(name = "Notas", description = "Lançamento e consulta de notas / boletim")
@SecurityRequirement(name = "bearerAuth")
public class NotaController {

    private final NotaService notaService;

    public NotaController(NotaService notaService) {
        this.notaService = notaService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADM', 'PROFESSOR')")
    @Operation(summary = "Lançar nota",
            description = "Lança nota final de um aluno em uma matéria. Professor só pode lançar para alunos da sua turma.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Nota lançada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou nota fora do intervalo [0, 10]",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Aluno ou matéria não encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Nota já lançada para este aluno/matéria",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<NotaResponse> lancar(@Valid @RequestBody LancarNotaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notaService.lancar(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADM', 'PROFESSOR')")
    @Operation(summary = "Atualizar nota",
            description = "Atualiza a nota final já lançada. O campo aprovado é recalculado automaticamente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nota atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Nota fora do intervalo [0, 10]",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Nota não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<NotaResponse> atualizar(@PathVariable Integer id,
                                                   @Valid @RequestBody AtualizarNotaRequest request) {
        return ResponseEntity.ok(notaService.atualizar(id, request));
    }

    @GetMapping("/aluno/{idAluno}")
    @PreAuthorize("hasAnyRole('ADM', 'PROFESSOR')")
    @Operation(summary = "Boletim por aluno",
            description = "Retorna o boletim completo de um aluno com resumo de aprovações.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Boletim retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<BoletimResponse> boletimPorAluno(@PathVariable Integer idAluno) {
        return ResponseEntity.ok(notaService.boletimPorAluno(idAluno));
    }

    @GetMapping("/turma/{idTurma}")
    @PreAuthorize("hasAnyRole('ADM', 'PROFESSOR')")
    @Operation(summary = "Notas por turma",
            description = "Retorna todas as notas lançadas para alunos de uma turma.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<List<NotaResponse>> notasPorTurma(@PathVariable Integer idTurma) {
        return ResponseEntity.ok(notaService.notasPorTurma(idTurma));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ALUNO')")
    @Operation(summary = "Meu boletim",
            description = "Aluno consulta o próprio boletim sem precisar informar ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Boletim retornado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso permitido apenas para ALUNO",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<BoletimResponse> meuBoletim() {
        return ResponseEntity.ok(notaService.meuBoletim());
    }
}
