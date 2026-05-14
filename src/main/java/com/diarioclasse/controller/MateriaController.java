package com.diarioclasse.controller;

import com.diarioclasse.dto.request.AtualizarMateriaRequest;
import com.diarioclasse.dto.request.CriarMateriaRequest;
import com.diarioclasse.dto.response.ErroResponse;
import com.diarioclasse.dto.response.MateriaResponse;
import com.diarioclasse.dto.response.PaginaResponse;
import com.diarioclasse.service.MateriaService;
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

import java.net.URI;

@RestController
@RequestMapping("/api/v1/materias")
@Tag(name = "Matérias", description = "Gerenciamento de matérias")
@SecurityRequirement(name = "bearerAuth")
public class MateriaController {

    private final MateriaService materiaService;

    public MateriaController(MateriaService materiaService) {
        this.materiaService = materiaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADM', 'PROFESSOR')")
    @Operation(summary = "Listar matérias", description = "ADM vê todas as matérias. PROFESSOR vê apenas as matérias para as quais está habilitado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<PaginaResponse<MateriaResponse>> listar(
            @PageableDefault(size = 20) Pageable pageable,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                PaginaResponse.from(materiaService.listar(pageable), request.getRequestURL().toString()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADM', 'PROFESSOR')")
    @Operation(summary = "Buscar matéria por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Matéria encontrada"),
            @ApiResponse(responseCode = "404", description = "Matéria não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<MateriaResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(materiaService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADM')")
    @Operation(summary = "Criar matéria")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Matéria criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Já existe matéria com este nome",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<MateriaResponse> criar(@Valid @RequestBody CriarMateriaRequest request,
                                                  HttpServletRequest httpRequest) {
        MateriaResponse response = materiaService.criar(request);
        URI location = URI.create(httpRequest.getRequestURL() + "/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADM')")
    @Operation(summary = "Atualizar matéria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Matéria atualizada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Matéria não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Já existe matéria com este nome",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<MateriaResponse> atualizar(@PathVariable Integer id,
                                                      @Valid @RequestBody AtualizarMateriaRequest request) {
        return ResponseEntity.ok(materiaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADM')")
    @Operation(summary = "Remover matéria")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Matéria removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Matéria não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Matéria possui professores vinculados",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ResponseEntity<Void> remover(@PathVariable Integer id) {
        materiaService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
