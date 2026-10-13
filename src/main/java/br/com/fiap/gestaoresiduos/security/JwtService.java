package br.com.fiap.gestaoresiduos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey chave;

    @Getter
    private final long expiracaoMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiracaoMs
    ) {
        byte[] bytes = Decoders.BASE64.decode(secret);
        this.chave = Keys.hmacShaKeyFor(bytes);
        this.expiracaoMs = expiracaoMs;
    }

    public String gerarToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        // adiciona a role nas claims do token
        if (!user.getAuthorities().isEmpty()) {
            claims.put("role", user.getAuthorities().iterator().next().getAuthority());
        }

        Date agora = new Date();
        Date expira = new Date(agora.getTime() + expiracaoMs);

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(agora)
                .expiration(expira)
                .signWith(chave)
                .compact();
    }

    public String extrairEmail(String token) {
        return getClaims(token).getSubject();
    }

    public boolean tokenValido(String token, UserDetails user) {
        String email = extrairEmail(token);
        Date expiracao = getClaims(token).getExpiration();
        return email.equals(user.getUsername()) && expiracao.after(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
