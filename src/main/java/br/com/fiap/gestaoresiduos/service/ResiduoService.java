package br.com.fiap.gestaoresiduos.service;

import br.com.fiap.gestaoresiduos.dto.residuo.ResiduoRequest;
import br.com.fiap.gestaoresiduos.dto.residuo.ResiduoResponse;
import br.com.fiap.gestaoresiduos.dto.residuo.ResiduosPorTipoResponse;
import br.com.fiap.gestaoresiduos.exception.ResourceNotFoundException;
import br.com.fiap.gestaoresiduos.mapper.ResiduoMapper;
import br.com.fiap.gestaoresiduos.model.PontoColeta;
import br.com.fiap.gestaoresiduos.model.Residuo;
import br.com.fiap.gestaoresiduos.model.enums.TipoResiduo;
import br.com.fiap.gestaoresiduos.repository.ResiduoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResiduoService {

    private final ResiduoRepository residuoRepository;
    private final PontoColetaService pontoColetaService;

    public Page<ResiduoResponse> listar(Pageable pageable) {
        return residuoRepository.findAll(pageable).map(ResiduoMapper::toResponse);
    }

    public ResiduoResponse buscarPorId(Long id) {
        Residuo r = residuoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Residuo", id));
        return ResiduoMapper.toResponse(r);
    }

    @Transactional
    public ResiduoResponse criar(ResiduoRequest req) {
        PontoColeta ponto = pontoColetaService.buscarEntidade(req.getPontoColetaId());
        Residuo salvo = residuoRepository.save(ResiduoMapper.toEntity(req, ponto));
        return ResiduoMapper.toResponse(salvo);
    }

    @Transactional
    public ResiduoResponse atualizar(Long id, ResiduoRequest req) {
        Residuo residuo = residuoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Residuo", id));
        PontoColeta ponto = pontoColetaService.buscarEntidade(req.getPontoColetaId());
        ResiduoMapper.atualizar(residuo, req, ponto);
        return ResiduoMapper.toResponse(residuoRepository.save(residuo));
    }

    @Transactional
    public void deletar(Long id) {
        Residuo residuo = residuoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Residuo", id));
        residuoRepository.delete(residuo);
    }

    public ResiduosPorTipoResponse listarPorTipo(TipoResiduo tipo) {
        List<Residuo> residuos = residuoRepository.findByTipo(tipo);

        // soma o peso total
        BigDecimal pesoTotal = BigDecimal.ZERO;
        List<ResiduoResponse> dtos = new ArrayList<>();
        for (Residuo r : residuos) {
            pesoTotal = pesoTotal.add(r.getPesoKg());
            dtos.add(ResiduoMapper.toResponse(r));
        }

        ResiduosPorTipoResponse resp = new ResiduosPorTipoResponse();
        resp.setTipo(tipo);
        resp.setQuantidadeRegistros(residuos.size());
        resp.setPesoTotalKg(pesoTotal);
        resp.setResiduos(dtos);
        return resp;
    }
}
