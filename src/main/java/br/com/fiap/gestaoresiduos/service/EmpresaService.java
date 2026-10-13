package br.com.fiap.gestaoresiduos.service;

import br.com.fiap.gestaoresiduos.dto.empresa.EmpresaRequest;
import br.com.fiap.gestaoresiduos.dto.empresa.EmpresaResponse;
import br.com.fiap.gestaoresiduos.exception.ResourceNotFoundException;
import br.com.fiap.gestaoresiduos.mapper.EmpresaMapper;
import br.com.fiap.gestaoresiduos.model.Empresa;
import br.com.fiap.gestaoresiduos.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public Page<EmpresaResponse> listar(Pageable pageable) {
        return empresaRepository.findAll(pageable).map(EmpresaMapper::toResponse);
    }

    public EmpresaResponse buscarPorId(Long id) {
        return EmpresaMapper.toResponse(buscarEntidade(id));
    }

    public Empresa buscarEntidade(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", id));
    }

    @Transactional
    public EmpresaResponse criar(EmpresaRequest req) {
        log.info("Criando empresa: {}", req.getNome());

        if (empresaRepository.existsByCnpj(req.getCnpj())) {
            throw new DataIntegrityViolationException("CNPJ ja cadastrado");
        }

        Empresa nova = EmpresaMapper.toEntity(req);
        nova.setDataCadastro(LocalDateTime.now());
        return EmpresaMapper.toResponse(empresaRepository.save(nova));
    }

    @Transactional
    public EmpresaResponse atualizar(Long id, EmpresaRequest req) {
        Empresa empresa = buscarEntidade(id);

        // se o CNPJ mudou, verifica se nao bate com outro existente
        if (!empresa.getCnpj().equals(req.getCnpj()) && empresaRepository.existsByCnpj(req.getCnpj())) {
            throw new DataIntegrityViolationException("CNPJ ja cadastrado");
        }

        EmpresaMapper.atualizar(empresa, req);
        return EmpresaMapper.toResponse(empresaRepository.save(empresa));
    }

    @Transactional
    public void deletar(Long id) {
        Empresa empresa = buscarEntidade(id);
        empresaRepository.delete(empresa);
    }
}
