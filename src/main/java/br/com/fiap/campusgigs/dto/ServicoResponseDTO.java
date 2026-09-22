package br.com.fiap.campusgigs.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.fiap.campusgigs.model.Servico;
import br.com.fiap.campusgigs.model.enums.SituacaoServico;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServicoResponseDTO {

	private Long id;
	private Long prestadorId;
	private String prestadorNome;
	private String titulo;
	private String descricao;
	private String categoria;
	private BigDecimal preco;
	private SituacaoServico situacao;
	private LocalDateTime criadoEm;

	public static ServicoResponseDTO from(Servico servico) {
		ServicoResponseDTO dto = new ServicoResponseDTO();
		dto.setId(servico.getId());
		dto.setPrestadorId(servico.getPrestador().getId());
		dto.setPrestadorNome(servico.getPrestador().getNome());
		dto.setTitulo(servico.getTitulo());
		dto.setDescricao(servico.getDescricao());
		dto.setCategoria(servico.getCategoria());
		dto.setPreco(servico.getPreco());
		dto.setSituacao(servico.getSituacao());
		dto.setCriadoEm(servico.getCriadoEm());
		return dto;
	}
}
