package br.com.fiap.gestaoresiduos.mapper;

import br.com.fiap.gestaoresiduos.dto.coleta.AgendarColetaRequest;
import br.com.fiap.gestaoresiduos.dto.coleta.ColetaRequest;
import br.com.fiap.gestaoresiduos.dto.coleta.ColetaResponse;
import br.com.fiap.gestaoresiduos.model.Coleta;
import br.com.fiap.gestaoresiduos.model.PontoColeta;
import br.com.fiap.gestaoresiduos.model.enums.StatusColeta;

import java.math.BigDecimal;

public class ColetaMapper {

    public static Coleta toEntity(ColetaRequest req, PontoColeta ponto) {
        Coleta c = new Coleta();
        c.setDataColeta(req.getDataColeta());
        c.setPesoTotalKg(req.getPesoTotalKg() != null ? req.getPesoTotalKg() : BigDecimal.ZERO);
        c.setStatus(req.getStatus());
        c.setPontoColeta(ponto);
        c.setObservacoes(req.getObservacoes());
        return c;
    }

    public static Coleta agendar(AgendarColetaRequest req, PontoColeta ponto) {
        Coleta c = new Coleta();
        c.setDataColeta(req.getDataColeta());
        c.setPesoTotalKg(BigDecimal.ZERO);
        c.setStatus(StatusColeta.AGENDADA);
        c.setPontoColeta(ponto);
        c.setObservacoes(req.getObservacoes());
        return c;
    }

    public static ColetaResponse toResponse(Coleta c) {
        ColetaResponse r = new ColetaResponse();
        r.setId(c.getId());
        r.setDataColeta(c.getDataColeta());
        r.setPesoTotalKg(c.getPesoTotalKg());
        r.setStatus(c.getStatus());
        if (c.getPontoColeta() != null) {
            r.setPontoColetaId(c.getPontoColeta().getId());
            r.setPontoColetaNome(c.getPontoColeta().getNome());
        }
        r.setObservacoes(c.getObservacoes());
        return r;
    }

    public static void atualizar(Coleta c, ColetaRequest req, PontoColeta ponto) {
        c.setDataColeta(req.getDataColeta());
        if (req.getPesoTotalKg() != null) {
            c.setPesoTotalKg(req.getPesoTotalKg());
        }
        c.setStatus(req.getStatus());
        c.setPontoColeta(ponto);
        c.setObservacoes(req.getObservacoes());
    }
}
