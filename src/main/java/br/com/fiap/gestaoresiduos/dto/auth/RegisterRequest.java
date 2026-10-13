package br.com.fiap.gestaoresiduos.dto.auth;

import br.com.fiap.gestaoresiduos.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 100)
    private String nome;

    @NotBlank(message = "Email e obrigatorio")
    @Email(message = "Email invalido")
    private String email;

    @NotBlank(message = "Senha e obrigatoria")
    @Size(min = 6, max = 60, message = "Senha precisa ter entre 6 e 60 caracteres")
    private String senha;

    @NotNull(message = "Role e obrigatoria (USER ou ADMIN)")
    private Role role;
}
