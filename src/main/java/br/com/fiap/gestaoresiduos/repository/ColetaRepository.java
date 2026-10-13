package br.com.fiap.gestaoresiduos.repository;

import br.com.fiap.gestaoresiduos.model.Coleta;
import br.com.fiap.gestaoresiduos.model.enums.StatusColeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ColetaRepository extends JpaRepository<Coleta, Long> {

    @Query("SELECT c FROM Coleta c WHERE c.pontoColeta.empresa.id = :empresaId ORDER BY c.dataColeta DESC")
    Page<Coleta> findHistoricoPorEmpresa(@Param("empresaId") Long empresaId, Pageable pageable);

    List<Coleta> findByStatus(StatusColeta status);

    @Query("SELECT COALESCE(SUM(c.pesoTotalKg), 0) FROM Coleta c WHERE c.status = :status")
    BigDecimal somarPesoPorStatus(@Param("status") StatusColeta status);
}
