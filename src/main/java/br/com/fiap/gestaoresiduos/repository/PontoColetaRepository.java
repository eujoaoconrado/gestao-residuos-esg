package br.com.fiap.gestaoresiduos.repository;

import br.com.fiap.gestaoresiduos.model.PontoColeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PontoColetaRepository extends JpaRepository<PontoColeta, Long> {

    // busca pontos que ja atingiram pelo menos 80% da capacidade
    @Query("SELECT p FROM PontoColeta p WHERE (p.nivelAtualKg / p.capacidadeMaximaKg) >= 0.80 ORDER BY (p.nivelAtualKg / p.capacidadeMaximaKg) DESC")
    List<PontoColeta> findPontosEmAlerta();

    List<PontoColeta> findByEmpresaId(Long empresaId);
}
