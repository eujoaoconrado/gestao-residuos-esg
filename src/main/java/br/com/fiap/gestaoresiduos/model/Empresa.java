package br.com.fiap.gestaoresiduos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "EMPRESAS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EMPRESA")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 150)
    private String nome;

    @Column(name = "CNPJ", nullable = false, unique = true, length = 18)
    private String cnpj;

    @Column(name = "ENDERECO", length = 200)
    private String endereco;

    @Column(name = "TELEFONE", length = 20)
    private String telefone;

    @Column(name = "DATA_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;
}
