package br.com.fiap.campusgigs.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.campusgigs.dto.LoginRequestDTO;
import br.com.fiap.campusgigs.dto.UsuarioResponseDTO;
import br.com.fiap.campusgigs.exception.UnauthorizedException;
import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.repository.UsuarioRepository;

@Service
public class AuthService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional(readOnly = true)
	public UsuarioResponseDTO login(LoginRequestDTO dto) {
		Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
				.filter(encontrado -> passwordEncoder.matches(dto.getSenha(), encontrado.getSenha()))
				.orElseThrow(() -> new UnauthorizedException("E-mail ou senha inválidos"));
		return UsuarioResponseDTO.from(usuario);
	}
}
