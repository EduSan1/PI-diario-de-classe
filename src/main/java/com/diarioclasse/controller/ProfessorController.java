package com.diarioclasse.controller;

import com.diarioclasse.dto.request.AtualizarProfessorRequest;
import com.diarioclasse.dto.response.ErroResponse;
import com.diarioclasse.dto.response.PaginaResponse;
import com.diarioclasse.dto.response.ProfessorResponse;
import com.diarioclasse.service.ProfessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/professores")
@Tag(name = "Professores", description = "Gerenciamento de professores — acesso restrito a ADM")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADM')")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @GetMapping
    @Operation(summary = "Listar professores", description = "Retorna todos os professores com suas matérias de qualificação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<PaginaResponse<ProfessorResponse>> listar(
            @PageableDefault(size = 20) Pageable pageable,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                PaginaResponse.from(professorService.listar(pageable), request.getRequestURL().toString()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar professor por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Professor encontrado"),
            @ApiResponse(responseCode = "404", description = "Professor não encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<ProfessorResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(professorService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar professor",
            description = "Atualiza dados do professor. `idsMaterias` é replace completo: `null` mantém, `[]` remove todas, `[1,2]` substitui.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Professor atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Professor ou matéria não encontrado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<ProfessorResponse> atualizar(@PathVariable Integer id,
                                                        @RequestBody AtualizarProfessorRequest request) {
        return ResponseEntity.ok(professorService.atualizar(id, request));
    }
}
