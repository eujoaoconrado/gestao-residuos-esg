package br.com.fiap.gestaoresiduos.dto.coleta;

import br.com.fiap.gestaoresiduos.model.enums.StatusColeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColetaResponse {

    private Long id;
    private LocalDateTime dataColeta;
    private BigDecimal pesoTotalKg;
    private StatusColeta status;
    private Long pontoColetaId;
    private String pontoColetaNome;
    private String observacoes;
}
