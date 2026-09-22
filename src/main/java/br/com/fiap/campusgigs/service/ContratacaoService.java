package br.com.fiap.campusgigs.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.campusgigs.dto.ContratacaoResponseDTO;
import br.com.fiap.campusgigs.exception.BusinessRuleException;
import br.com.fiap.campusgigs.model.Contratacao;
import br.com.fiap.campusgigs.model.Servico;
import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.model.enums.SituacaoContratacao;
import br.com.fiap.campusgigs.model.enums.SituacaoServico;
import br.com.fiap.campusgigs.repository.ContratacaoRepository;
import br.com.fiap.campusgigs.security.UsuarioAutenticadoService;

@Service
public class ContratacaoService {

	private final ContratacaoRepository contratacaoRepository;
	private final ServicoService servicoService;
	private final UsuarioAutenticadoService usuarioAutenticadoService;

	public ContratacaoService(
			ContratacaoRepository contratacaoRepository,
			ServicoService servicoService,
			UsuarioAutenticadoService usuarioAutenticadoService) {
		this.contratacaoRepository = contratacaoRepository;
		this.servicoService = servicoService;
		this.usuarioAutenticadoService = usuarioAutenticadoService;
	}

	@Transactional
	public ContratacaoResponseDTO contratar(Long servicoId) {
		Usuario contratante = usuarioAutenticadoService.exigirEntidade();
		Servico servico = servicoService.buscarComPrestador(servicoId);

		if (servico.getSituacao() != SituacaoServico.ATIVO) {
			throw new BusinessRuleException("Não é possível contratar um serviço que não está ativo");
		}
		if (servico.getPrestador().getId().equals(contratante.getId())) {
			throw new BusinessRuleException("Não é possível contratar o próprio serviço");
		}
		if (contratacaoRepository.existsByServicoIdAndContratanteIdAndSituacaoIn(
				servicoId,
				contratante.getId(),
				List.of(SituacaoContratacao.SOLICITADA, SituacaoContratacao.ACEITA))) {
			throw new BusinessRuleException("Você já contratou este serviço");
		}

		Contratacao contratacao = Contratacao.builder()
				.servico(servico)
				.contratante(contratante)
				.situacao(SituacaoContratacao.SOLICITADA)
				.build();
		return ContratacaoResponseDTO.from(contratacaoRepository.save(contratacao));
	}
}
