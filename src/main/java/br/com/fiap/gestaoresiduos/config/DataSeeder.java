package br.com.fiap.gestaoresiduos.config;

import br.com.fiap.gestaoresiduos.model.Usuario;
import br.com.fiap.gestaoresiduos.model.enums.Role;
import br.com.fiap.gestaoresiduos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Cria os usuarios padroes (admin e user) na primeira execucao
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByEmail("admin@fiap.com")) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@fiap.com");
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            usuarioRepository.save(admin);
            log.info("Usuario admin criado: admin@fiap.com / admin123");
        }

        if (!usuarioRepository.existsByEmail("user@fiap.com")) {
            Usuario user = new Usuario();
            user.setNome("Usuario Padrao");
            user.setEmail("user@fiap.com");
            user.setSenha(passwordEncoder.encode("user123"));
            user.setRole(Role.USER);
            usuarioRepository.save(user);
            log.info("Usuario padrao criado: user@fiap.com / user123");
        }
    }
}
