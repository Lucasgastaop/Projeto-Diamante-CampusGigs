package br.com.fiap.campusgigs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

	private String token;
	private String tipo;
	private UsuarioResponseDTO usuario;
}
