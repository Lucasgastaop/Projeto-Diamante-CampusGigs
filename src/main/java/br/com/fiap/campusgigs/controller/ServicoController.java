package br.com.fiap.campusgigs.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.fiap.campusgigs.dto.ContratacaoResponseDTO;
import br.com.fiap.campusgigs.dto.ServicoRequestDTO;
import br.com.fiap.campusgigs.dto.ServicoResponseDTO;
import br.com.fiap.campusgigs.service.ContratacaoService;
import br.com.fiap.campusgigs.service.ServicoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

	private final ServicoService servicoService;
	private final ContratacaoService contratacaoService;

	public ServicoController(ServicoService servicoService, ContratacaoService contratacaoService) {
		this.servicoService = servicoService;
		this.contratacaoService = contratacaoService;
	}

	@PostMapping
	public ResponseEntity<ServicoResponseDTO> publicar(@Valid @RequestBody ServicoRequestDTO dto) {
		ServicoResponseDTO criado = servicoService.publicar(dto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(criado.getId())
				.toUri();
		return ResponseEntity.created(location).body(criado);
	}

	@GetMapping
	public List<ServicoResponseDTO> listarPublicados() {
		return servicoService.listarPublicados();
	}

	@GetMapping("/{id}")
	public ServicoResponseDTO buscarPorId(@PathVariable Long id) {
		return servicoService.buscarPorId(id);
	}

	@PatchMapping("/{id}/encerrar")
	public ServicoResponseDTO encerrar(@PathVariable Long id) {
		return servicoService.encerrar(id);
	}

	@PostMapping("/{id}/contratacoes")
	public ResponseEntity<ContratacaoResponseDTO> contratar(@PathVariable Long id) {
		ContratacaoResponseDTO criada = contratacaoService.contratar(id);
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/servicos/{servicoId}/contratacoes/{id}")
				.buildAndExpand(id, criada.getId())
				.toUri();
		return ResponseEntity.created(location).body(criada);
	}
}
