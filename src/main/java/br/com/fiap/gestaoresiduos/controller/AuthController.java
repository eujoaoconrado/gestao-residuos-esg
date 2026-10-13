package br.com.fiap.gestaoresiduos.controller;

import br.com.fiap.gestaoresiduos.dto.auth.AuthResponse;
import br.com.fiap.gestaoresiduos.dto.auth.LoginRequest;
import br.com.fiap.gestaoresiduos.dto.auth.RegisterRequest;
import br.com.fiap.gestaoresiduos.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacao")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.registrar(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.autenticar(req));
    }
}
