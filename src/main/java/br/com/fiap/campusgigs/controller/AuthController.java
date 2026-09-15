package br.com.fiap.campusgigs.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.campusgigs.dto.LoginRequestDTO;
import br.com.fiap.campusgigs.dto.LoginResponseDTO;
import br.com.fiap.campusgigs.service.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO dto) {
		return authService.login(dto);
	}
}
