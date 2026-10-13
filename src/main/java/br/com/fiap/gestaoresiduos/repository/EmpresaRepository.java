package br.com.fiap.gestaoresiduos.repository;

import br.com.fiap.gestaoresiduos.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Optional<Empresa> findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);
}
