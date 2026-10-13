package br.com.fiap.gestaoresiduos.service;

import br.com.fiap.gestaoresiduos.dto.coleta.AgendarColetaRequest;
import br.com.fiap.gestaoresiduos.dto.coleta.ColetaRequest;
import br.com.fiap.gestaoresiduos.dto.coleta.ColetaResponse;
import br.com.fiap.gestaoresiduos.exception.ResourceNotFoundException;
import br.com.fiap.gestaoresiduos.mapper.ColetaMapper;
import br.com.fiap.gestaoresiduos.model.Coleta;
import br.com.fiap.gestaoresiduos.model.PontoColeta;
import br.com.fiap.gestaoresiduos.model.enums.StatusColeta;
import br.com.fiap.gestaoresiduos.repository.ColetaRepository;
import br.com.fiap.gestaoresiduos.repository.PontoColetaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ColetaService {

    private final ColetaRepository coletaRepository;
    private final PontoColetaRepository pontoColetaRepository;
    private final PontoColetaService pontoColetaService;

    public Page<ColetaResponse> listar(Pageable pageable) {
        return coletaRepository.findAll(pageable).map(ColetaMapper::toResponse);
    }

    public ColetaResponse buscarPorId(Long id) {
        return ColetaMapper.toResponse(buscarEntidade(id));
    }

    private Coleta buscarEntidade(Long id) {
        return coletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coleta", id));
    }

    @Transactional
    public ColetaResponse criar(ColetaRequest req) {
        PontoColeta ponto = pontoColetaService.buscarEntidade(req.getPontoColetaId());
        Coleta salva = coletaRepository.save(ColetaMapper.toEntity(req, ponto));
        return ColetaMapper.toResponse(salva);
    }

    @Transactional
    public ColetaResponse atualizar(Long id, ColetaRequest req) {
        Coleta coleta = buscarEntidade(id);
        PontoColeta ponto = pontoColetaService.buscarEntidade(req.getPontoColetaId());
        ColetaMapper.atualizar(coleta, req, ponto);
        return ColetaMapper.toResponse(coletaRepository.save(coleta));
    }

    @Transactional
    public void deletar(Long id) {
        coletaRepository.delete(buscarEntidade(id));
    }

    public Page<ColetaResponse> historicoPorEmpresa(Long empresaId, Pageable pageable) {
        return coletaRepository.findHistoricoPorEmpresa(empresaId, pageable)
                .map(ColetaMapper::toResponse);
    }

    @Transactional
    public ColetaResponse agendar(AgendarColetaRequest req) {
        log.info("Agendando coleta no ponto {} para {}", req.getPontoColetaId(), req.getDataColeta());
        PontoColeta ponto = pontoColetaService.buscarEntidade(req.getPontoColetaId());
        Coleta agendada = ColetaMapper.agendar(req, ponto);
        return ColetaMapper.toResponse(coletaRepository.save(agendada));
    }

    @Transactional
    public ColetaResponse realizar(Long id) {
        Coleta coleta = buscarEntidade(id);

        if (coleta.getStatus() == StatusColeta.REALIZADA) {
            throw new IllegalArgumentException("Esta coleta ja foi realizada");
        }
        if (coleta.getStatus() == StatusColeta.CANCELADA) {
            throw new IllegalArgumentException("Coleta cancelada nao pode ser realizada");
        }

        PontoColeta ponto = coleta.getPontoColeta();
        // o peso coletado eh tudo que estava no ponto
        BigDecimal coletado = ponto.getNivelAtualKg();
        coleta.setPesoTotalKg(coletado);
        coleta.setStatus(StatusColeta.REALIZADA);
        coleta.setDataColeta(LocalDateTime.now());

        // esvazia o ponto apos a coleta
        ponto.setNivelAtualKg(BigDecimal.ZERO);
        pontoColetaRepository.save(ponto);

        return ColetaMapper.toResponse(coletaRepository.save(coleta));
    }
}
