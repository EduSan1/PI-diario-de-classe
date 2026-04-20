package com.diarioclasse.controller;

import com.diarioclasse.dto.request.CorrigirPresencaRequest;
import com.diarioclasse.dto.request.LancarPresencaRequest;
import com.diarioclasse.dto.response.ErroResponse;
import com.diarioclasse.dto.response.FrequenciaResponse;
import com.diarioclasse.dto.response.PresencaAuditoriaResponse;
import com.diarioclasse.dto.response.PresencaRegistradaResponse;
import com.diarioclasse.dto.response.PresencaResponse;
import com.diarioclasse.service.PresencaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/presencas")
@Tag(name = "Presenças", description = "Registro e consulta de presenças")
@SecurityRequirement(name = "bearerAuth")
public class PresencaController {

    private final PresencaService presencaService;

    public PresencaController(PresencaService presencaService) {
        this.presencaService = presencaService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADM', 'PROFESSOR')")
    @Operation(summary = "Lançar chamada",
            description = "Registra presença de múltiplos alunos em uma matéria. Professor só pode lançar para alunos da sua turma e matéria.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Presenças registradas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou data futura",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Aluno ou matéria não encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Presença duplicada para aluno/matéria/data",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<PresencaRegistradaResponse> lancar(@Valid @RequestBody LancarPresencaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(presencaService.lancar(request));
    }

    @GetMapping("/aluno/{idAluno}")
    @PreAuthorize("hasAnyRole('ADM', 'PROFESSOR')")
    @Operation(summary = "Listar presenças de um aluno",
            description = "ADM acessa qualquer aluno. Professor só acessa alunos da sua turma.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<List<PresencaResponse>> listarPorAluno(@PathVariable Integer idAluno) {
        return ResponseEntity.ok(presencaService.listarPorAluno(idAluno));
    }

    @GetMapping("/aluno/{idAluno}/materia/{idMateria}")
    @PreAuthorize("hasAnyRole('ADM', 'PROFESSOR')")
    @Operation(summary = "Listar presenças de aluno em uma matéria",
            description = "ADM acessa qualquer aluno. Professor só acessa alunos da sua turma.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<List<PresencaResponse>> listarPorAlunoEMateria(
            @PathVariable Integer idAluno,
            @PathVariable Integer idMateria) {
        return ResponseEntity.ok(presencaService.listarPorAlunoEMateria(idAluno, idMateria));
    }

    @GetMapping("/turma/{idTurma}/data/{data}")
    @PreAuthorize("hasAnyRole('ADM', 'PROFESSOR')")
    @Operation(summary = "Chamada do dia por turma",
            description = "Retorna todas as presenças de uma turma em uma data. Professor só acessa turmas onde leciona.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<List<PresencaResponse>> listarPorTurmaEData(
            @PathVariable Integer idTurma,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(presencaService.listarPorTurmaEData(idTurma, data));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ALUNO')")
    @Operation(summary = "Minha frequência",
            description = "Aluno consulta o resumo da própria frequência por matéria.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Frequência retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<List<FrequenciaResponse>> minhaFrequencia() {
        return ResponseEntity.ok(presencaService.minhaFrequencia());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADM', 'PROFESSOR')")
    @Operation(summary = "Corrigir presença",
            description = "ADM pode corrigir qualquer presença. Professor só pode corrigir presenças do dia atual ou do dia anterior, e apenas da sua turma/matéria.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Presença corrigida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado ou presença fora do prazo editável",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Presença não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<PresencaResponse> corrigir(
            @PathVariable Integer id,
            @Valid @RequestBody CorrigirPresencaRequest request) {
        return ResponseEntity.ok(presencaService.corrigir(id, request));
    }

    @GetMapping("/{id}/auditoria")
    @PreAuthorize("hasRole('ADM')")
    @Operation(summary = "Auditoria de presença",
            description = "Retorna os dados completos de auditoria de um registro de presença, incluindo datas e usuários de criação/alteração. Exclusivo para ADM.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados de auditoria retornados com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Presença não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<PresencaAuditoriaResponse> auditoria(@PathVariable Integer id) {
        return ResponseEntity.ok(presencaService.auditoria(id));
    }
}
