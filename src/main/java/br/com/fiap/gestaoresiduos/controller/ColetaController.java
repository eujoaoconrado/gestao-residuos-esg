package br.com.fiap.gestaoresiduos.controller;

import br.com.fiap.gestaoresiduos.dto.coleta.AgendarColetaRequest;
import br.com.fiap.gestaoresiduos.dto.coleta.ColetaRequest;
import br.com.fiap.gestaoresiduos.dto.coleta.ColetaResponse;
import br.com.fiap.gestaoresiduos.service.ColetaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coletas")
@RequiredArgsConstructor
@Tag(name = "Coletas")
public class ColetaController {

    private final ColetaService coletaService;

    @GetMapping
    public ResponseEntity<Page<ColetaResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(coletaService.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColetaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(coletaService.buscarPorId(id));
    }

    @GetMapping("/historico/{empresaId}")
    public ResponseEntity<Page<ColetaResponse>> historico(@PathVariable Long empresaId, Pageable pageable) {
        return ResponseEntity.ok(coletaService.historicoPorEmpresa(empresaId, pageable));
    }

    @PostMapping
    public ResponseEntity<ColetaResponse> criar(@Valid @RequestBody ColetaRequest req) {
        return ResponseEntity.status(201).body(coletaService.criar(req));
    }

    @PostMapping("/agendar")
    public ResponseEntity<ColetaResponse> agendar(@Valid @RequestBody AgendarColetaRequest req) {
        return ResponseEntity.status(201).body(coletaService.agendar(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColetaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody ColetaRequest req) {
        return ResponseEntity.ok(coletaService.atualizar(id, req));
    }

    @PutMapping("/{id}/realizar")
    public ResponseEntity<ColetaResponse> realizar(@PathVariable Long id) {
        return ResponseEntity.ok(coletaService.realizar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        coletaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
