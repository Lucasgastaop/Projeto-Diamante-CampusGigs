package br.com.fiap.campusgigs.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import br.com.fiap.campusgigs.dto.ViaCepResponseDTO;

public interface ViaCepClient {

	@GetExchange("/ws/{cep}/json/")
	ViaCepResponseDTO buscar(@PathVariable String cep);
}
