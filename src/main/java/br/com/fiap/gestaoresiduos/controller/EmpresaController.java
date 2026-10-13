package br.com.fiap.gestaoresiduos.controller;

import br.com.fiap.gestaoresiduos.dto.empresa.EmpresaRequest;
import br.com.fiap.gestaoresiduos.dto.empresa.EmpresaResponse;
import br.com.fiap.gestaoresiduos.service.EmpresaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empresas")
@RequiredArgsConstructor
@Tag(name = "Empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping
    public ResponseEntity<Page<EmpresaResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(empresaService.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<EmpresaResponse> criar(@Valid @RequestBody EmpresaRequest req) {
        EmpresaResponse criada = empresaService.criar(req);
        return ResponseEntity.status(201).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody EmpresaRequest req) {
        return ResponseEntity.ok(empresaService.atualizar(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        empresaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
