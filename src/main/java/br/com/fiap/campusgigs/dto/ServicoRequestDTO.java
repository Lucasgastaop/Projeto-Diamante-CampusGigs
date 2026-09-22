package br.com.fiap.campusgigs.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServicoRequestDTO {

	@NotBlank(message = "Título é obrigatório")
	@Size(max = 150, message = "Título deve ter no máximo 150 caracteres")
	private String titulo;

	@NotBlank(message = "Descrição é obrigatória")
	private String descricao;

	@NotBlank(message = "Categoria é obrigatória")
	@Size(max = 80, message = "Categoria deve ter no máximo 80 caracteres")
	private String categoria;

	@NotNull(message = "Preço é obrigatório")
	@DecimalMin(value = "0.00", message = "Preço deve ser maior ou igual a zero")
	private BigDecimal preco;
}
