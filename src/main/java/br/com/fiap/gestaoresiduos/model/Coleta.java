package br.com.fiap.gestaoresiduos.model;

import br.com.fiap.gestaoresiduos.model.enums.StatusColeta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "COLETAS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "pontoColeta")
public class Coleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COLETA")
    private Long id;

    @Column(name = "DATA_COLETA", nullable = false)
    private LocalDateTime dataColeta;

    @Column(name = "PESO_TOTAL_KG", precision = 10, scale = 2)
    private BigDecimal pesoTotalKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusColeta status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PONTO", nullable = false)
    private PontoColeta pontoColeta;

    @Column(name = "OBSERVACOES", length = 500)
    private String observacoes;
}
