package br.com.fiap.gestaoresiduos.dto.coleta;

import br.com.fiap.gestaoresiduos.model.enums.StatusColeta;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ColetaRequest {

    @NotNull
    private LocalDateTime dataColeta;

    @PositiveOrZero
    private BigDecimal pesoTotalKg;

    @NotNull
    private StatusColeta status;

    @NotNull
    @Positive
    private Long pontoColetaId;

    @Size(max = 500)
    private String observacoes;
}
