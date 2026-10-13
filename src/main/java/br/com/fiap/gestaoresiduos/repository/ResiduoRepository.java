package br.com.fiap.gestaoresiduos.repository;

import br.com.fiap.gestaoresiduos.model.Residuo;
import br.com.fiap.gestaoresiduos.model.enums.TipoResiduo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ResiduoRepository extends JpaRepository<Residuo, Long> {

    List<Residuo> findByTipo(TipoResiduo tipo);

    // soma do peso de residuos por tipo, util pros indicadores
    @Query("SELECT r.tipo, SUM(r.pesoKg) FROM Residuo r GROUP BY r.tipo")
    List<Object[]> somarPesoPorTipo();

    @Query("SELECT COALESCE(SUM(r.pesoKg), 0) FROM Residuo r")
    BigDecimal somarPesoTotal();
}
