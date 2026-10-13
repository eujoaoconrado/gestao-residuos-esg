package br.com.fiap.gestaoresiduos.mapper;

import br.com.fiap.gestaoresiduos.dto.empresa.EmpresaRequest;
import br.com.fiap.gestaoresiduos.dto.empresa.EmpresaResponse;
import br.com.fiap.gestaoresiduos.model.Empresa;

public class EmpresaMapper {

    public static Empresa toEntity(EmpresaRequest req) {
        Empresa e = new Empresa();
        e.setNome(req.getNome());
        e.setCnpj(req.getCnpj());
        e.setEndereco(req.getEndereco());
        e.setTelefone(req.getTelefone());
        return e;
    }

    public static EmpresaResponse toResponse(Empresa e) {
        EmpresaResponse r = new EmpresaResponse();
        r.setId(e.getId());
        r.setNome(e.getNome());
        r.setCnpj(e.getCnpj());
        r.setEndereco(e.getEndereco());
        r.setTelefone(e.getTelefone());
        r.setDataCadastro(e.getDataCadastro());
        return r;
    }

    public static void atualizar(Empresa e, EmpresaRequest req) {
        e.setNome(req.getNome());
        e.setCnpj(req.getCnpj());
        e.setEndereco(req.getEndereco());
        e.setTelefone(req.getTelefone());
    }
}
