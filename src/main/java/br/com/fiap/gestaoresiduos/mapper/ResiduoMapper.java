package br.com.fiap.gestaoresiduos.mapper;

import br.com.fiap.gestaoresiduos.dto.residuo.ResiduoRequest;
import br.com.fiap.gestaoresiduos.dto.residuo.ResiduoResponse;
import br.com.fiap.gestaoresiduos.model.PontoColeta;
import br.com.fiap.gestaoresiduos.model.Residuo;

import java.time.LocalDateTime;

public class ResiduoMapper {

    public static Residuo toEntity(ResiduoRequest req, PontoColeta ponto) {
        Residuo r = new Residuo();
        r.setTipo(req.getTipo());
        r.setDescricao(req.getDescricao());
        r.setPesoKg(req.getPesoKg());
        r.setDataColeta(req.getDataColeta() != null ? req.getDataColeta() : LocalDateTime.now());
        r.setPontoColeta(ponto);
        return r;
    }

    public static ResiduoResponse toResponse(Residuo r) {
        ResiduoResponse dto = new ResiduoResponse();
        dto.setId(r.getId());
        dto.setTipo(r.getTipo());
        dto.setDescricao(r.getDescricao());
        dto.setPesoKg(r.getPesoKg());
        dto.setDataColeta(r.getDataColeta());
        if (r.getPontoColeta() != null) {
            dto.setPontoColetaId(r.getPontoColeta().getId());
            dto.setPontoColetaNome(r.getPontoColeta().getNome());
        }
        return dto;
    }

    public static void atualizar(Residuo r, ResiduoRequest req, PontoColeta ponto) {
        r.setTipo(req.getTipo());
        r.setDescricao(req.getDescricao());
        r.setPesoKg(req.getPesoKg());
        if (req.getDataColeta() != null) {
            r.setDataColeta(req.getDataColeta());
        }
        r.setPontoColeta(ponto);
    }
}
