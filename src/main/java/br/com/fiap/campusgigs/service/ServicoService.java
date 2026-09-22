package br.com.fiap.campusgigs.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.campusgigs.dto.ServicoRequestDTO;
import br.com.fiap.campusgigs.dto.ServicoResponseDTO;
import br.com.fiap.campusgigs.exception.BusinessRuleException;
import br.com.fiap.campusgigs.exception.ForbiddenOperationException;
import br.com.fiap.campusgigs.exception.ResourceNotFoundException;
import br.com.fiap.campusgigs.model.Servico;
import br.com.fiap.campusgigs.model.Usuario;
import br.com.fiap.campusgigs.model.enums.SituacaoServico;
import br.com.fiap.campusgigs.repository.ServicoRepository;
import br.com.fiap.campusgigs.security.UsuarioAutenticadoService;

@Service
public class ServicoService {

	private final ServicoRepository servicoRepository;
	private final UsuarioAutenticadoService usuarioAutenticadoService;

	public ServicoService(
			ServicoRepository servicoRepository,
			UsuarioAutenticadoService usuarioAutenticadoService) {
		this.servicoRepository = servicoRepository;
		this.usuarioAutenticadoService = usuarioAutenticadoService;
	}

	@Transactional
	public ServicoResponseDTO publicar(ServicoRequestDTO dto) {
		Usuario prestador = usuarioAutenticadoService.exigirEntidade();
		Servico servico = Servico.builder()
				.prestador(prestador)
				.titulo(dto.getTitulo())
				.descricao(dto.getDescricao())
				.categoria(dto.getCategoria())
				.preco(dto.getPreco())
				.situacao(SituacaoServico.ATIVO)
				.build();
		return ServicoResponseDTO.from(servicoRepository.save(servico));
	}

	@Transactional(readOnly = true)
	public List<ServicoResponseDTO> listarPublicados() {
		return servicoRepository.findBySituacaoComPrestador(SituacaoServico.ATIVO).stream()
				.map(ServicoResponseDTO::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public ServicoResponseDTO buscarPorId(Long id) {
		return ServicoResponseDTO.from(buscarComPrestador(id));
	}

	@Transactional
	public ServicoResponseDTO encerrar(Long id) {
		Servico servico = buscarComPrestador(id);
		if (servico.getSituacao() == SituacaoServico.ENCERRADO) {
			throw new BusinessRuleException("Serviço já está encerrado");
		}

		Long usuarioId = usuarioAutenticadoService.exigirUsuario().getId();
		boolean dono = servico.getPrestador().getId().equals(usuarioId);
		if (!dono && !usuarioAutenticadoService.isAdmin()) {
			throw new ForbiddenOperationException("Apenas o prestador pode encerrar este serviço");
		}

		servico.setSituacao(SituacaoServico.ENCERRADO);
		return ServicoResponseDTO.from(servico);
	}

	@Transactional(readOnly = true)
	public Servico buscarComPrestador(Long id) {
		return servicoRepository.findByIdComPrestador(id)
				.orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado: " + id));
	}
}
