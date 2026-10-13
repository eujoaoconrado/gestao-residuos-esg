package br.com.fiap.gestaoresiduos.model;

import br.com.fiap.gestaoresiduos.model.enums.TipoResiduo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "RESIDUOS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "pontoColeta")
public class Residuo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RESIDUO")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoResiduo tipo;

    @Column(name = "DESCRICAO", length = 300)
    private String descricao;

    @Column(name = "PESO_KG", nullable = false, precision = 10, scale = 2)
    private BigDecimal pesoKg;

    @Column(name = "DATA_COLETA", nullable = false)
    private LocalDateTime dataColeta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PONTO", nullable = false)
    private PontoColeta pontoColeta;
}
