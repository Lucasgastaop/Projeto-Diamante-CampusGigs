package br.com.fiap.campusgigs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViaCepResponseDTO {

	private String logradouro;
	private String bairro;
	private String localidade;
	private String uf;
	private Object erro;

	public boolean possuiErro() {
		return Boolean.TRUE.equals(erro) || "true".equalsIgnoreCase(String.valueOf(erro));
	}
}
