package br.com.fiap.gestaoresiduos.config;

import br.com.fiap.gestaoresiduos.model.Coleta;
import br.com.fiap.gestaoresiduos.model.Empresa;
import br.com.fiap.gestaoresiduos.model.PontoColeta;
import br.com.fiap.gestaoresiduos.model.Residuo;
import br.com.fiap.gestaoresiduos.model.enums.StatusColeta;
import br.com.fiap.gestaoresiduos.model.enums.TipoResiduo;
import br.com.fiap.gestaoresiduos.repository.ColetaRepository;
import br.com.fiap.gestaoresiduos.repository.EmpresaRepository;
import br.com.fiap.gestaoresiduos.repository.PontoColetaRepository;
import br.com.fiap.gestaoresiduos.repository.ResiduoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Popula dados de exemplo quando rodar com profile dev (H2)
// No perfil de producao quem cuida disso eh o Flyway V2.
@Slf4j
@Component
@Profile("dev")
@Order(2)
@RequiredArgsConstructor
public class DevDataSeeder implements CommandLineRunner {

    private final EmpresaRepository empresaRepository;
    private final PontoColetaRepository pontoColetaRepository;
    private final ResiduoRepository residuoRepository;
    private final ColetaRepository coletaRepository;

    @Override
    public void run(String... args) {
        if (empresaRepository.count() > 0) {
            return;
        }

        log.info("Populando dados de exemplo (profile dev)");

        // empresas
        Empresa e1 = new Empresa();
        e1.setNome("EcoBrasil Reciclagem LTDA");
        e1.setCnpj("12.345.678/0001-90");
        e1.setEndereco("Av. Paulista, 1000 - Sao Paulo/SP");
        e1.setTelefone("(11) 4002-8922");
        e1.setDataCadastro(LocalDateTime.now());
        empresaRepository.save(e1);

        Empresa e2 = new Empresa();
        e2.setNome("Verde Coleta Seletiva ME");
        e2.setCnpj("98.765.432/0001-10");
        e2.setEndereco("Rua das Flores, 250 - Campinas/SP");
        e2.setTelefone("(19) 3232-1010");
        e2.setDataCadastro(LocalDateTime.now());
        empresaRepository.save(e2);

        // pontos de coleta
        PontoColeta p1 = novoPonto("Ecoponto Centro", "Rua XV de Novembro, 100 - Centro",
                TipoResiduo.PLASTICO, "500.00", "425.50", e1);
        PontoColeta p2 = novoPonto("Ecoponto Norte", "Av. Brasil, 450 - Bairro Norte",
                TipoResiduo.PAPEL, "300.00", "120.00", e1);
        PontoColeta p3 = novoPonto("Ecoponto Sul", "Rua das Palmeiras, 22 - Bairro Sul",
                TipoResiduo.VIDRO, "400.00", "380.00", e2);
        PontoColeta p4 = novoPonto("Ecoponto Leste", "Rua XV de Novembro, 77 - Leste",
                TipoResiduo.METAL, "600.00", "50.00", e2);

        pontoColetaRepository.save(p1);
        pontoColetaRepository.save(p2);
        pontoColetaRepository.save(p3);
        pontoColetaRepository.save(p4);

        // residuos
        residuoRepository.save(novoResiduo(TipoResiduo.PLASTICO, "Garrafas PET de escolas", "50.50", p1));
        residuoRepository.save(novoResiduo(TipoResiduo.PLASTICO, "Embalagens de mercado", "75.00", p1));
        residuoRepository.save(novoResiduo(TipoResiduo.PAPEL, "Caixas de papelao", "40.00", p2));
        residuoRepository.save(novoResiduo(TipoResiduo.VIDRO, "Garrafas e potes", "80.00", p3));
        residuoRepository.save(novoResiduo(TipoResiduo.METAL, "Latas de aluminio", "25.00", p4));

        // coletas
        coletaRepository.save(novaColeta(LocalDateTime.now().minusDays(10), "320.00", StatusColeta.REALIZADA, p1, "Coleta PET centro"));
        coletaRepository.save(novaColeta(LocalDateTime.now().minusDays(7), "180.50", StatusColeta.REALIZADA, p2, "Papelao reciclado"));
        coletaRepository.save(novaColeta(LocalDateTime.now().plusDays(2), "0", StatusColeta.AGENDADA, p3, "Coleta urgente"));
        coletaRepository.save(novaColeta(LocalDateTime.now().plusDays(5), "0", StatusColeta.AGENDADA, p1, "Coleta semanal"));
        coletaRepository.save(novaColeta(LocalDateTime.now().minusDays(1), "0", StatusColeta.CANCELADA, p4, "Cancelada por falta de motorista"));

        log.info("Seed dev concluido. 2 empresas, 4 pontos, 5 residuos, 5 coletas");
    }

    private PontoColeta novoPonto(String nome, String endereco, TipoResiduo tipo, String capacidade, String nivel, Empresa empresa) {
        PontoColeta p = new PontoColeta();
        p.setNome(nome);
        p.setEndereco(endereco);
        p.setTipoResiduo(tipo);
        p.setCapacidadeMaximaKg(new BigDecimal(capacidade));
        p.setNivelAtualKg(new BigDecimal(nivel));
        p.setEmpresa(empresa);
        return p;
    }

    private Residuo novoResiduo(TipoResiduo tipo, String descricao, String peso, PontoColeta ponto) {
        Residuo r = new Residuo();
        r.setTipo(tipo);
        r.setDescricao(descricao);
        r.setPesoKg(new BigDecimal(peso));
        r.setDataColeta(LocalDateTime.now());
        r.setPontoColeta(ponto);
        return r;
    }

    private Coleta novaColeta(LocalDateTime data, String peso, StatusColeta status, PontoColeta ponto, String obs) {
        Coleta c = new Coleta();
        c.setDataColeta(data);
        c.setPesoTotalKg(new BigDecimal(peso));
        c.setStatus(status);
        c.setPontoColeta(ponto);
        c.setObservacoes(obs);
        return c;
    }
}
