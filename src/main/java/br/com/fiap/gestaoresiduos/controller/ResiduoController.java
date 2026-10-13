package br.com.fiap.gestaoresiduos.controller;

import br.com.fiap.gestaoresiduos.dto.residuo.ResiduoRequest;
import br.com.fiap.gestaoresiduos.dto.residuo.ResiduoResponse;
import br.com.fiap.gestaoresiduos.dto.residuo.ResiduosPorTipoResponse;
import br.com.fiap.gestaoresiduos.model.enums.TipoResiduo;
import br.com.fiap.gestaoresiduos.service.ResiduoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/residuos")
@RequiredArgsConstructor
@Tag(name = "Residuos")
public class ResiduoController {

    private final ResiduoService residuoService;

    @GetMapping
    public ResponseEntity<Page<ResiduoResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(residuoService.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResiduoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(residuoService.buscarPorId(id));
    }

    @GetMapping("/por-tipo/{tipo}")
    public ResponseEntity<ResiduosPorTipoResponse> porTipo(@PathVariable TipoResiduo tipo) {
        return ResponseEntity.ok(residuoService.listarPorTipo(tipo));
    }

    @PostMapping
    public ResponseEntity<ResiduoResponse> criar(@Valid @RequestBody ResiduoRequest req) {
        return ResponseEntity.status(201).body(residuoService.criar(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResiduoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody ResiduoRequest req) {
        return ResponseEntity.ok(residuoService.atualizar(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        residuoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
