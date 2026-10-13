package br.com.fiap.gestaoresiduos.service;

import br.com.fiap.gestaoresiduos.dto.pontocoleta.AlertaPontoResponse;
import br.com.fiap.gestaoresiduos.dto.pontocoleta.PontoColetaRequest;
import br.com.fiap.gestaoresiduos.dto.pontocoleta.PontoColetaResponse;
import br.com.fiap.gestaoresiduos.exception.ResourceNotFoundException;
import br.com.fiap.gestaoresiduos.mapper.PontoColetaMapper;
import br.com.fiap.gestaoresiduos.model.Empresa;
import br.com.fiap.gestaoresiduos.model.PontoColeta;
import br.com.fiap.gestaoresiduos.repository.PontoColetaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PontoColetaService {

    private final PontoColetaRepository pontoColetaRepository;
    private final EmpresaService empresaService;

    public Page<PontoColetaResponse> listar(Pageable pageable) {
        return pontoColetaRepository.findAll(pageable).map(PontoColetaMapper::toResponse);
    }

    public PontoColetaResponse buscarPorId(Long id) {
        return PontoColetaMapper.toResponse(buscarEntidade(id));
    }

    public PontoColeta buscarEntidade(Long id) {
        return pontoColetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PontoColeta", id));
    }

    @Transactional
    public PontoColetaResponse criar(PontoColetaRequest req) {
        log.info("Criando ponto de coleta: {}", req.getNome());
        Empresa empresa = empresaService.buscarEntidade(req.getEmpresaId());
        PontoColeta novo = PontoColetaMapper.toEntity(req, empresa);
        return PontoColetaMapper.toResponse(pontoColetaRepository.save(novo));
    }

    @Transactional
    public PontoColetaResponse atualizar(Long id, PontoColetaRequest req) {
        PontoColeta ponto = buscarEntidade(id);
        Empresa empresa = empresaService.buscarEntidade(req.getEmpresaId());
        PontoColetaMapper.atualizar(ponto, req, empresa);
        return PontoColetaMapper.toResponse(pontoColetaRepository.save(ponto));
    }

    @Transactional
    public void deletar(Long id) {
        PontoColeta ponto = buscarEntidade(id);
        pontoColetaRepository.delete(ponto);
    }

    public List<AlertaPontoResponse> listarAlertas() {
        List<PontoColeta> pontos = pontoColetaRepository.findPontosEmAlerta();
        List<AlertaPontoResponse> alertas = new ArrayList<>();
        for (PontoColeta p : pontos) {
            alertas.add(PontoColetaMapper.toAlerta(p));
        }
        return alertas;
    }
}
