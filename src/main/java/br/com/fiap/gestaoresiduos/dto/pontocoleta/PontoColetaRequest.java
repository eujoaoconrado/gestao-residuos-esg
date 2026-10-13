package br.com.fiap.gestaoresiduos.dto.pontocoleta;

import br.com.fiap.gestaoresiduos.model.enums.TipoResiduo;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PontoColetaRequest {

    @NotBlank
    @Size(max = 100)
    private String nome;

    @NotBlank
    @Size(max = 200)
    private String endereco;

    @NotNull
    private TipoResiduo tipoResiduo;

    @NotNull
    @Positive
    private BigDecimal capacidadeMaximaKg;

    @PositiveOrZero
    private BigDecimal nivelAtualKg;

    @NotNull
    @Positive
    private Long empresaId;
}
