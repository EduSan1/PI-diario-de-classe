package com.diarioclasse.controller;

import com.diarioclasse.dto.request.AtualizarTurmaRequest;
import com.diarioclasse.dto.request.CriarTurmaRequest;
import com.diarioclasse.dto.response.ErroResponse;
import com.diarioclasse.dto.response.TurmaResponse;
import com.diarioclasse.service.TurmaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/turmas")
@Tag(name = "Turmas", description = "Gerenciamento de turmas — acesso restrito a ADM")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADM')")
public class TurmaController {

    private final TurmaService turmaService;

    public TurmaController(TurmaService turmaService) {
        this.turmaService = turmaService;
    }

    @GetMapping
    @Operation(summary = "Listar turmas", description = "Retorna todas as turmas com paginação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<Page<TurmaResponse>> listar(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(turmaService.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar turma por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Turma encontrada"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<TurmaResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(turmaService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar turma")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Turma criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Professor regente não encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Turma com mesma série/letra/ano já existe",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<TurmaResponse> criar(@Valid @RequestBody CriarTurmaRequest request) {
        TurmaResponse response = turmaService.criar(request);
        URI location = URI.create("/api/v1/turmas/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar turma")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Turma atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Turma ou professor não encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflito de unicidade",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<TurmaResponse> atualizar(@PathVariable Integer id,
                                                    @Valid @RequestBody AtualizarTurmaRequest request) {
        return ResponseEntity.ok(turmaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover turma")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Turma removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Turma possui alunos vinculados",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<Void> remover(@PathVariable Integer id) {
        turmaService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
