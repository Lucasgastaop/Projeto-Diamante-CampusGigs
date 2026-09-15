package br.com.fiap.campusgigs.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.campusgigs.dto.LoginRequestDTO;
import br.com.fiap.campusgigs.dto.LoginResponseDTO;
import br.com.fiap.campusgigs.dto.UsuarioResponseDTO;
import br.com.fiap.campusgigs.exception.UnauthorizedException;
import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.repository.UsuarioRepository;
import br.com.fiap.campusgigs.security.JwtService;

@Service
public class AuthService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(
			UsuarioRepository usuarioRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@Transactional(readOnly = true)
	public LoginResponseDTO login(LoginRequestDTO dto) {
		Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
				.filter(encontrado -> passwordEncoder.matches(dto.getSenha(), encontrado.getSenha()))
				.orElseThrow(() -> new UnauthorizedException("E-mail ou senha inválidos"));

		return new LoginResponseDTO(
				jwtService.generateToken(usuario),
				"Bearer",
				UsuarioResponseDTO.from(usuario));
	}
}
