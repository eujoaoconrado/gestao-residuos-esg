package br.com.fiap.gestaoresiduos.dto.pontocoleta;

import br.com.fiap.gestaoresiduos.model.enums.TipoResiduo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertaPontoResponse {

    private Long idPonto;
    private String nome;
    private String endereco;
    private TipoResiduo tipoResiduo;
    private BigDecimal capacidadeMaximaKg;
    private BigDecimal nivelAtualKg;
    private double percentualOcupacao;
    private String nivelAlerta;
    private String mensagem;
}
