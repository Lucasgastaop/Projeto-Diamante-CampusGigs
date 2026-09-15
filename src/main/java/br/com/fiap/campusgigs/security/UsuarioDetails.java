package br.com.fiap.campusgigs.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.model.enums.PapelUsuario;

public class UsuarioDetails implements UserDetails {

	private final Long id;
	private final String nome;
	private final String email;
	private final String senha;
	private final PapelUsuario papel;

	public UsuarioDetails(Long id, String nome, String email, String senha, PapelUsuario papel) {
		this.id = id;
		this.nome = nome;
		this.email = email;
		this.senha = senha;
		this.papel = papel;
	}

	public static UsuarioDetails from(Usuario usuario) {
		return new UsuarioDetails(
				usuario.getId(),
				usuario.getNome(),
				usuario.getEmail(),
				usuario.getSenha(),
				usuario.getPapel());
	}

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public PapelUsuario getPapel() {
		return papel;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(papel.asRole()));
	}

	@Override
	public String getPassword() {
		return senha;
	}

	@Override
	public String getUsername() {
		return email;
	}
}
