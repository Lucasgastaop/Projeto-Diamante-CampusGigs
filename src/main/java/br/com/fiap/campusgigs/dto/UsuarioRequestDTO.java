package br.com.fiap.campusgigs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {

	@NotBlank(message = "Nome é obrigatório")
	@Size(max = 120, message = "Nome deve ter no máximo 120 caracteres")
	private String nome;

	@NotBlank(message = "E-mail é obrigatório")
	@Email(message = "E-mail inválido")
	@Size(max = 120, message = "E-mail deve ter no máximo 120 caracteres")
	private String email;

	@NotBlank(message = "Senha é obrigatória")
	@Size(min = 8, max = 72, message = "Senha deve ter entre 8 e 72 caracteres")
	private String senha;

	@Size(min = 8, max = 8, message = "CEP deve ter 8 dígitos")
	@Pattern(regexp = "\\d{8}", message = "CEP deve conter apenas números")
	private String cep;

	@Size(max = 150)
	private String logradouro;

	@Size(max = 100)
	private String bairro;

	@Size(max = 100)
	private String cidade;

	@Size(min = 2, max = 2, message = "UF deve ter 2 letras")
	private String uf;
}
