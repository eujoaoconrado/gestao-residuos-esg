package br.com.fiap.gestaoresiduos.controller;

import br.com.fiap.gestaoresiduos.dto.pontocoleta.AlertaPontoResponse;
import br.com.fiap.gestaoresiduos.dto.pontocoleta.PontoColetaRequest;
import br.com.fiap.gestaoresiduos.dto.pontocoleta.PontoColetaResponse;
import br.com.fiap.gestaoresiduos.service.PontoColetaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pontos-coleta")
@RequiredArgsConstructor
@Tag(name = "Pontos de Coleta")
public class PontoColetaController {

    private final PontoColetaService pontoColetaService;

    @GetMapping
    public ResponseEntity<Page<PontoColetaResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(pontoColetaService.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontoColetaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(pontoColetaService.buscarPorId(id));
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<AlertaPontoResponse>> alertas() {
        return ResponseEntity.ok(pontoColetaService.listarAlertas());
    }

    @PostMapping
    public ResponseEntity<PontoColetaResponse> criar(@Valid @RequestBody PontoColetaRequest req) {
        return ResponseEntity.status(201).body(pontoColetaService.criar(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PontoColetaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody PontoColetaRequest req) {
        return ResponseEntity.ok(pontoColetaService.atualizar(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pontoColetaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
