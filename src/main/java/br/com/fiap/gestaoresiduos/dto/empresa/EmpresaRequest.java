package br.com.fiap.gestaoresiduos.dto.empresa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmpresaRequest {

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 150)
    private String nome;

    @NotBlank(message = "CNPJ e obrigatorio")
    @Pattern(
            regexp = "^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$|^\\d{14}$",
            message = "CNPJ deve estar no formato 00.000.000/0000-00 ou ter 14 digitos"
    )
    private String cnpj;

    @Size(max = 200)
    private String endereco;

    @Size(max = 20)
    private String telefone;
}
