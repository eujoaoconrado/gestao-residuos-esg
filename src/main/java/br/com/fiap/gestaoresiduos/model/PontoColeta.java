package br.com.fiap.gestaoresiduos.model;

import br.com.fiap.gestaoresiduos.model.enums.TipoResiduo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "PONTOS_COLETA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "empresa")
public class PontoColeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PONTO")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "ENDERECO", nullable = false, length = 200)
    private String endereco;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_RESIDUO", nullable = false, length = 20)
    private TipoResiduo tipoResiduo;

    @Column(name = "CAPACIDADE_MAXIMA_KG", nullable = false, precision = 10, scale = 2)
    private BigDecimal capacidadeMaximaKg;

    @Column(name = "NIVEL_ATUAL_KG", nullable = false, precision = 10, scale = 2)
    private BigDecimal nivelAtualKg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EMPRESA", nullable = false)
    private Empresa empresa;
}
