package br.com.fiap.gestaoresiduos.mapper;

import br.com.fiap.gestaoresiduos.dto.pontocoleta.AlertaPontoResponse;
import br.com.fiap.gestaoresiduos.dto.pontocoleta.PontoColetaRequest;
import br.com.fiap.gestaoresiduos.dto.pontocoleta.PontoColetaResponse;
import br.com.fiap.gestaoresiduos.model.Empresa;
import br.com.fiap.gestaoresiduos.model.PontoColeta;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PontoColetaMapper {

    public static PontoColeta toEntity(PontoColetaRequest req, Empresa empresa) {
        PontoColeta p = new PontoColeta();
        p.setNome(req.getNome());
        p.setEndereco(req.getEndereco());
        p.setTipoResiduo(req.getTipoResiduo());
        p.setCapacidadeMaximaKg(req.getCapacidadeMaximaKg());
        p.setNivelAtualKg(req.getNivelAtualKg() != null ? req.getNivelAtualKg() : BigDecimal.ZERO);
        p.setEmpresa(empresa);
        return p;
    }

    public static PontoColetaResponse toResponse(PontoColeta p) {
        PontoColetaResponse r = new PontoColetaResponse();
        r.setId(p.getId());
        r.setNome(p.getNome());
        r.setEndereco(p.getEndereco());
        r.setTipoResiduo(p.getTipoResiduo());
        r.setCapacidadeMaximaKg(p.getCapacidadeMaximaKg());
        r.setNivelAtualKg(p.getNivelAtualKg());
        r.setPercentualOcupacao(calcularPercentual(p));
        if (p.getEmpresa() != null) {
            r.setEmpresaId(p.getEmpresa().getId());
            r.setEmpresaNome(p.getEmpresa().getNome());
        }
        return r;
    }

    public static AlertaPontoResponse toAlerta(PontoColeta p) {
        double pct = calcularPercentual(p);

        String nivel;
        String mensagem;
        if (pct >= 100.0) {
            nivel = "CRITICO";
            mensagem = "Ponto cheio, coleta urgente";
        } else if (pct >= 90.0) {
            nivel = "ALTO";
            mensagem = "Ponto com mais de 90 por cento da capacidade";
        } else {
            nivel = "MEDIO";
            mensagem = "Ponto atingiu 80 por cento da capacidade, agendar coleta";
        }

        AlertaPontoResponse a = new AlertaPontoResponse();
        a.setIdPonto(p.getId());
        a.setNome(p.getNome());
        a.setEndereco(p.getEndereco());
        a.setTipoResiduo(p.getTipoResiduo());
        a.setCapacidadeMaximaKg(p.getCapacidadeMaximaKg());
        a.setNivelAtualKg(p.getNivelAtualKg());
        a.setPercentualOcupacao(pct);
        a.setNivelAlerta(nivel);
        a.setMensagem(mensagem);
        return a;
    }

    public static void atualizar(PontoColeta p, PontoColetaRequest req, Empresa empresa) {
        p.setNome(req.getNome());
        p.setEndereco(req.getEndereco());
        p.setTipoResiduo(req.getTipoResiduo());
        p.setCapacidadeMaximaKg(req.getCapacidadeMaximaKg());
        if (req.getNivelAtualKg() != null) {
            p.setNivelAtualKg(req.getNivelAtualKg());
        }
        p.setEmpresa(empresa);
    }

    // calcula o percentual de ocupacao do ponto
    private static double calcularPercentual(PontoColeta p) {
        if (p.getCapacidadeMaximaKg() == null || p.getCapacidadeMaximaKg().signum() == 0) {
            return 0.0;
        }
        return p.getNivelAtualKg()
                .divide(p.getCapacidadeMaximaKg(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}
