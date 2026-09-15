package br.com.fiap.campusgigs.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.fiap.campusgigs.dto.UsuarioRequestDTO;
import br.com.fiap.campusgigs.dto.UsuarioResponseDTO;
import br.com.fiap.campusgigs.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

	private final UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@PostMapping
	public ResponseEntity<UsuarioResponseDTO> cadastrar(@Valid @RequestBody UsuarioRequestDTO dto) {
		UsuarioResponseDTO criado = usuarioService.cadastrar(dto);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(criado.getId())
				.toUri();
		return ResponseEntity.created(location).body(criado);
	}

	@GetMapping("/{id}")
	public UsuarioResponseDTO buscarPorId(@PathVariable Long id) {
		return usuarioService.buscarPorId(id);
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public List<UsuarioResponseDTO> listar() {
		return usuarioService.listar();
	}
}
