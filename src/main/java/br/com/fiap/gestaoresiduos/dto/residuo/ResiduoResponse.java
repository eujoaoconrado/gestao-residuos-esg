package br.com.fiap.gestaoresiduos.dto.residuo;

import br.com.fiap.gestaoresiduos.model.enums.TipoResiduo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResiduoResponse {

    private Long id;
    private TipoResiduo tipo;
    private String descricao;
    private BigDecimal pesoKg;
    private LocalDateTime dataColeta;
    private Long pontoColetaId;
    private String pontoColetaNome;
}
