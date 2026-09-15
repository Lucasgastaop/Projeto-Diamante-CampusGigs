package br.com.fiap.campusgigs.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.campusgigs.dto.UsuarioRequestDTO;
import br.com.fiap.campusgigs.dto.UsuarioResponseDTO;
import br.com.fiap.campusgigs.exception.DuplicateResourceException;
import br.com.fiap.campusgigs.exception.ResourceNotFoundException;
import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.model.enums.PapelUsuario;
import br.com.fiap.campusgigs.repository.UsuarioRepository;

@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {
		if (usuarioRepository.existsByEmail(dto.getEmail())) {
			throw new DuplicateResourceException("E-mail já cadastrado: " + dto.getEmail());
		}

		Usuario usuario = Usuario.builder()
				.nome(dto.getNome())
				.email(dto.getEmail())
				.senha(passwordEncoder.encode(dto.getSenha()))
				.papel(PapelUsuario.USER)
				.cep(dto.getCep())
				.logradouro(dto.getLogradouro())
				.bairro(dto.getBairro())
				.cidade(dto.getCidade())
				.uf(dto.getUf())
				.build();

		return UsuarioResponseDTO.from(usuarioRepository.save(usuario));
	}

	@Transactional(readOnly = true)
	public UsuarioResponseDTO buscarPorId(Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
		return UsuarioResponseDTO.from(usuario);
	}

	@Transactional(readOnly = true)
	public List<UsuarioResponseDTO> listar() {
		return usuarioRepository.findAll().stream()
				.map(UsuarioResponseDTO::from)
				.toList();
	}
}
