package com.diarioclasse.controller;

import com.diarioclasse.dto.request.AtualizarAlunoRequest;
import com.diarioclasse.dto.request.VincularTurmaRequest;
import com.diarioclasse.dto.response.AlunoDetalheResponse;
import com.diarioclasse.dto.response.AlunoResponse;
import com.diarioclasse.dto.response.AlunoResumoResponse;
import com.diarioclasse.dto.response.ErroResponse;
import com.diarioclasse.dto.response.PaginaResponse;
import com.diarioclasse.service.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alunos")
@Tag(name = "Alunos", description = "Gerenciamento de alunos")
@SecurityRequirement(name = "bearerAuth")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADM')")
    @Operation(summary = "Listar alunos", description = "Retorna todos os alunos com paginação. Requer perfil ADM.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<PaginaResponse<AlunoResponse>> listar(
            @PageableDefault(size = 20) Pageable pageable,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                PaginaResponse.from(alunoService.listar(pageable), request.getRequestURL().toString()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADM', 'PROFESSOR')")
    @Operation(summary = "Buscar aluno por ID",
            description = "ADM acessa qualquer aluno. PROFESSOR só acessa alunos da turma onde é regente. Dados de contato do responsável não são exibidos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aluno encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<AlunoDetalheResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(alunoService.buscarPorId(id));
    }

    @GetMapping("/turma/{idTurma}")
    @PreAuthorize("hasAnyRole('ADM', 'PROFESSOR')")
    @Operation(summary = "Listar alunos por turma",
            description = "ADM acessa qualquer turma. PROFESSOR só acessa turmas onde é regente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Professor sem acesso a esta turma",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<List<AlunoResumoResponse>> listarPorTurma(@PathVariable Integer idTurma) {
        return ResponseEntity.ok(alunoService.listarPorTurma(idTurma));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADM')")
    @Operation(summary = "Atualizar aluno",
            description = "Atualiza dados pessoais do aluno. Todos os campos são opcionais. RA e dados de acesso não são alteráveis aqui.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aluno atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<AlunoResponse> atualizar(@PathVariable Integer id,
                                                    @RequestBody AtualizarAlunoRequest request) {
        return ResponseEntity.ok(alunoService.atualizar(id, request));
    }

    @PatchMapping("/{id}/turma")
    @PreAuthorize("hasRole('ADM')")
    @Operation(summary = "Vincular aluno à turma",
            description = "Vincula o aluno a uma turma. `idTurma` é obrigatório — para remover um aluno da escola, desative o usuário via `DELETE /usuarios/{id}`.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aluno vinculado à turma com sucesso"),
            @ApiResponse(responseCode = "400", description = "idTurma é obrigatório",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Aluno ou turma não encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Turma atingiu o limite de alunos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<AlunoResponse> vincularTurma(@PathVariable Integer id,
                                                        @Valid @RequestBody VincularTurmaRequest request) {
        return ResponseEntity.ok(alunoService.vincularTurma(id, request));
    }
}
