package br.com.fiap.gestaoresiduos.service;

import br.com.fiap.gestaoresiduos.dto.indicadores.IndicadoresResponse;
import br.com.fiap.gestaoresiduos.model.enums.StatusColeta;
import br.com.fiap.gestaoresiduos.model.enums.TipoResiduo;
import br.com.fiap.gestaoresiduos.repository.ColetaRepository;
import br.com.fiap.gestaoresiduos.repository.PontoColetaRepository;
import br.com.fiap.gestaoresiduos.repository.ResiduoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IndicadoresService {

    private final ResiduoRepository residuoRepository;
    private final ColetaRepository coletaRepository;
    private final PontoColetaRepository pontoColetaRepository;

    public IndicadoresResponse gerar() {
        BigDecimal totalResiduos = residuoRepository.somarPesoTotal();
        BigDecimal totalColetado = coletaRepository.somarPesoPorStatus(StatusColeta.REALIZADA);

        if (totalResiduos == null) totalResiduos = BigDecimal.ZERO;
        if (totalColetado == null) totalColetado = BigDecimal.ZERO;

        // inicializa o map com todos os tipos zerados
        Map<TipoResiduo, BigDecimal> coletadoPorTipo = new HashMap<>();
        for (TipoResiduo t : TipoResiduo.values()) {
            coletadoPorTipo.put(t, BigDecimal.ZERO);
        }
        // preenche com os tipos que tem registros
        List<Object[]> resultados = residuoRepository.somarPesoPorTipo();
        for (Object[] linha : resultados) {
            TipoResiduo tipo = (TipoResiduo) linha[0];
            BigDecimal soma = (BigDecimal) linha[1];
            coletadoPorTipo.put(tipo, soma);
        }

        // calcula o percentual reciclado
        BigDecimal percentual = BigDecimal.ZERO;
        if (totalResiduos.signum() > 0) {
            percentual = totalColetado
                    .divide(totalResiduos, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        long totalPontos = pontoColetaRepository.count();
        long pontosAlerta = pontoColetaRepository.findPontosEmAlerta().size();

        long agendadas = coletaRepository.findByStatus(StatusColeta.AGENDADA).size();
        long realizadas = coletaRepository.findByStatus(StatusColeta.REALIZADA).size();
        long canceladas = coletaRepository.findByStatus(StatusColeta.CANCELADA).size();

        IndicadoresResponse resp = new IndicadoresResponse();
        resp.setTotalResiduosKg(totalResiduos);
        resp.setTotalColetadoKg(totalColetado);
        resp.setPercentualReciclado(percentual);
        resp.setTotalPontosColeta(totalPontos);
        resp.setTotalPontosEmAlerta(pontosAlerta);
        resp.setColetadoPorTipo(coletadoPorTipo);
        resp.setColetasAgendadas(agendadas);
        resp.setColetasRealizadas(realizadas);
        resp.setColetasCanceladas(canceladas);
        return resp;
    }
}
