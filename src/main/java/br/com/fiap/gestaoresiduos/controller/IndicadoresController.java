package br.com.fiap.gestaoresiduos.controller;

import br.com.fiap.gestaoresiduos.dto.indicadores.IndicadoresResponse;
import br.com.fiap.gestaoresiduos.service.IndicadoresService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/indicadores")
@RequiredArgsConstructor
@Tag(name = "Indicadores")
public class IndicadoresController {

    private final IndicadoresService indicadoresService;

    @GetMapping("/reciclagem")
    public ResponseEntity<IndicadoresResponse> reciclagem() {
        return ResponseEntity.ok(indicadoresService.gerar());
    }
}
