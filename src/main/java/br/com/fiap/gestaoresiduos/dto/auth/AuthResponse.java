package br.com.fiap.gestaoresiduos.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String tipo;
    private String email;
    private String role;
    private long expiracaoMs;
}
