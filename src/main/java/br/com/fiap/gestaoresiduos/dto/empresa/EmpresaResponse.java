package br.com.fiap.gestaoresiduos.dto.empresa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaResponse {

    private Long id;
    private String nome;
    private String cnpj;
    private String endereco;
    private String telefone;
    private LocalDateTime dataCadastro;
}
