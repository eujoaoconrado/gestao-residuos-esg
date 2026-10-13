package br.com.fiap.gestaoresiduos.dto.indicadores;

import br.com.fiap.gestaoresiduos.model.enums.TipoResiduo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndicadoresResponse {

    private BigDecimal totalResiduosKg;
    private BigDecimal totalColetadoKg;
    private BigDecimal percentualReciclado;
    private long totalPontosColeta;
    private long totalPontosEmAlerta;
    private Map<TipoResiduo, BigDecimal> coletadoPorTipo;
    private long coletasAgendadas;
    private long coletasRealizadas;
    private long coletasCanceladas;
}
