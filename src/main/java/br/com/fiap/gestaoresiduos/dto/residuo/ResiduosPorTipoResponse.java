package br.com.fiap.gestaoresiduos.dto.residuo;

import br.com.fiap.gestaoresiduos.model.enums.TipoResiduo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResiduosPorTipoResponse {

    private TipoResiduo tipo;
    private long quantidadeRegistros;
    private BigDecimal pesoTotalKg;
    private List<ResiduoResponse> residuos;
}
