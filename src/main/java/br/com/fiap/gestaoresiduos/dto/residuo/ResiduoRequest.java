package br.com.fiap.gestaoresiduos.dto.residuo;

import br.com.fiap.gestaoresiduos.model.enums.TipoResiduo;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ResiduoRequest {

    @NotNull
    private TipoResiduo tipo;

    @Size(max = 300)
    private String descricao;

    @NotNull
    @Positive
    private BigDecimal pesoKg;

    private LocalDateTime dataColeta;

    @NotNull
    @Positive
    private Long pontoColetaId;
}
