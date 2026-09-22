package br.com.fiap.campusgigs.dto;

import java.time.LocalDateTime;

import br.com.fiap.campusgigs.model.Contratacao;
import br.com.fiap.campusgigs.model.enums.SituacaoContratacao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContratacaoResponseDTO {

	private Long id;
	private Long servicoId;
	private String servicoTitulo;
	private Long prestadorId;
	private Long contratanteId;
	private String contratanteNome;
	private SituacaoContratacao situacao;
	private LocalDateTime criadoEm;

	public static ContratacaoResponseDTO from(Contratacao contratacao) {
		ContratacaoResponseDTO dto = new ContratacaoResponseDTO();
		dto.setId(contratacao.getId());
		dto.setServicoId(contratacao.getServico().getId());
		dto.setServicoTitulo(contratacao.getServico().getTitulo());
		dto.setPrestadorId(contratacao.getServico().getPrestador().getId());
		dto.setContratanteId(contratacao.getContratante().getId());
		dto.setContratanteNome(contratacao.getContratante().getNome());
		dto.setSituacao(contratacao.getSituacao());
		dto.setCriadoEm(contratacao.getCriadoEm());
		return dto;
	}
}
