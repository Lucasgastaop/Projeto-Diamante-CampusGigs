package br.com.fiap.campusgigs.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.fiap.campusgigs.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final SecretKey secretKey;
	private final long expirationMs;

	public JwtService(
			@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.expiration-ms}") long expirationMs) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expirationMs = expirationMs;
	}

	public String generateToken(Usuario usuario) {
		Date agora = new Date();
		Date expiracao = new Date(agora.getTime() + expirationMs);

		return Jwts.builder()
				.subject(usuario.getEmail())
				.claim("id", usuario.getId())
				.claim("papel", usuario.getPapel().name())
				.issuedAt(agora)
				.expiration(expiracao)
				.signWith(secretKey)
				.compact();
	}

	public String extractEmail(String token) {
		return parse(token).getSubject();
	}

	public boolean isValid(String token) {
		try {
			parse(token);
			return true;
		} catch (JwtException | IllegalArgumentException ex) {
			return false;
		}
	}

	private Claims parse(String token) {
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
