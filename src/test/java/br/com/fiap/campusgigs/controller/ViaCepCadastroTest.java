package br.com.fiap.campusgigs.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.fiap.campusgigs.client.ViaCepClient;
import br.com.fiap.campusgigs.dto.ViaCepResponseDTO;

@SpringBootTest
@AutoConfigureMockMvc
class ViaCepCadastroTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ViaCepClient viaCepClient;

	@Test
	void devePreencherEnderecoPeloCep() throws Exception {
		ViaCepResponseDTO endereco = new ViaCepResponseDTO();
		endereco.setLogradouro("Avenida Paulista");
		endereco.setBairro("Bela Vista");
		endereco.setLocalidade("São Paulo");
		endereco.setUf("SP");
		when(viaCepClient.buscar("01310100")).thenReturn(endereco);

		String email = emailUnico("cep");
		mockMvc.perform(post("/usuarios")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"nome": "Ana Souza",
									"email": "%s",
									"senha": "senha1234",
									"cep": "01310100"
								}
								""".formatted(email)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.cep").value("01310100"))
				.andExpect(jsonPath("$.logradouro").value("Avenida Paulista"))
				.andExpect(jsonPath("$.bairro").value("Bela Vista"))
				.andExpect(jsonPath("$.cidade").value("São Paulo"))
				.andExpect(jsonPath("$.uf").value("SP"));
	}

	@Test
	void deveRecusarCepInexistente() throws Exception {
		ViaCepResponseDTO erro = new ViaCepResponseDTO();
		erro.setErro("true");
		when(viaCepClient.buscar("00000000")).thenReturn(erro);

		mockMvc.perform(post("/usuarios")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"nome": "Ana Souza",
									"email": "%s",
									"senha": "senha1234",
									"cep": "00000000"
								}
								""".formatted(emailUnico("cepinvalido"))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("CEP não encontrado: 00000000"));
	}

	private String emailUnico(String prefixo) {
		return prefixo + "+" + UUID.randomUUID() + "@fiap.com.br";
	}
}
