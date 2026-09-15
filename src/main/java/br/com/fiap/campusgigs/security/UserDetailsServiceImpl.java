package br.com.fiap.campusgigs.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.campusgigs.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UsuarioRepository usuarioRepository;

	public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return usuarioRepository.findByEmail(username)
				.map(UsuarioDetails::from)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
	}
}
