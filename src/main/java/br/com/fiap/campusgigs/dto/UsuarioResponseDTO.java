package br.com.fiap.campusgigs.dto;

import java.time.LocalDateTime;

import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.model.enums.PapelUsuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

	private Long id;
	private String nome;
	private String email;
	private PapelUsuario papel;
	private String cep;
	private String logradouro;
	private String bairro;
	private String cidade;
	private String uf;
	private LocalDateTime criadoEm;

	public static UsuarioResponseDTO from(Usuario usuario) {
		UsuarioResponseDTO dto = new UsuarioResponseDTO();
		dto.setId(usuario.getId());
		dto.setNome(usuario.getNome());
		dto.setEmail(usuario.getEmail());
		dto.setPapel(usuario.getPapel());
		dto.setCep(usuario.getCep());
		dto.setLogradouro(usuario.getLogradouro());
		dto.setBairro(usuario.getBairro());
		dto.setCidade(usuario.getCidade());
		dto.setUf(usuario.getUf());
		dto.setCriadoEm(usuario.getCriadoEm());
		return dto;
	}
}
