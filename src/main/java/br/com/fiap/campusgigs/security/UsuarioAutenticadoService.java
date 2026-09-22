package br.com.fiap.campusgigs.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.fiap.campusgigs.exception.ResourceNotFoundException;
import br.com.fiap.campusgigs.exception.UnauthorizedException;
import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.model.enums.PapelUsuario;
import br.com.fiap.campusgigs.repository.UsuarioRepository;

@Service
public class UsuarioAutenticadoService {

	private final UsuarioRepository usuarioRepository;

	public UsuarioAutenticadoService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	public UsuarioDetails exigirUsuario() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof UsuarioDetails detalhes)) {
			throw new UnauthorizedException("Não autenticado");
		}
		return detalhes;
	}

	public Usuario exigirEntidade() {
		UsuarioDetails detalhes = exigirUsuario();
		return usuarioRepository.findById(detalhes.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + detalhes.getId()));
	}

	public boolean isAdmin() {
		return exigirUsuario().getPapel() == PapelUsuario.ADMIN;
	}
}
