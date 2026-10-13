package br.com.fiap.gestaoresiduos.dto.coleta;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AgendarColetaRequest {

    @NotNull
    @Future(message = "Data deve ser no futuro")
    private LocalDateTime dataColeta;

    @NotNull
    @Positive
    private Long pontoColetaId;

    @Size(max = 500)
    private String observacoes;
}
