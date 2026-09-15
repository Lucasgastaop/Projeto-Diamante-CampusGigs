package br.com.fiap.campusgigs.security;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.model.enums.PapelUsuario;
import br.com.fiap.campusgigs.repository.UsuarioRepository;

@Component
public class AdminUserInitializer implements ApplicationRunner {

	public static final String ADMIN_EMAIL = "admin@campusgigs.com";
	public static final String ADMIN_SENHA = "admin1234";

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	public AdminUserInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (usuarioRepository.existsByEmail(ADMIN_EMAIL)) {
			return;
		}

		usuarioRepository.save(Usuario.builder()
				.nome("Administrador")
				.email(ADMIN_EMAIL)
				.senha(passwordEncoder.encode(ADMIN_SENHA))
				.papel(PapelUsuario.ADMIN)
				.build());
	}
}
