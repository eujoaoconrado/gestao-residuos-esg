package br.com.fiap.gestaoresiduos.service;

import br.com.fiap.gestaoresiduos.dto.auth.AuthResponse;
import br.com.fiap.gestaoresiduos.dto.auth.LoginRequest;
import br.com.fiap.gestaoresiduos.dto.auth.RegisterRequest;
import br.com.fiap.gestaoresiduos.model.Usuario;
import br.com.fiap.gestaoresiduos.repository.UsuarioRepository;
import br.com.fiap.gestaoresiduos.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse registrar(RegisterRequest req) {
        log.info("Registrando novo usuario: {}", req.getEmail());

        if (usuarioRepository.existsByEmail(req.getEmail())) {
            throw new DataIntegrityViolationException("Ja existe um usuario com este email");
        }

        Usuario u = new Usuario();
        u.setNome(req.getNome());
        u.setEmail(req.getEmail());
        u.setSenha(passwordEncoder.encode(req.getSenha()));
        u.setRole(req.getRole());
        usuarioRepository.save(u);

        String token = jwtService.gerarToken(u);
        return new AuthResponse(token, "Bearer", u.getEmail(), u.getRole().name(), jwtService.getExpiracaoMs());
    }

    public AuthResponse autenticar(LoginRequest req) {
        log.info("Login: {}", req.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getSenha())
        );

        Usuario u = usuarioRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));

        String token = jwtService.gerarToken(u);
        return new AuthResponse(token, "Bearer", u.getEmail(), u.getRole().name(), jwtService.getExpiracaoMs());
    }
}
