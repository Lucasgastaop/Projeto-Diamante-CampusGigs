package br.com.fiap.campusgigs.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class ServicoContratacaoTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void devePublicarServicoAutenticado() throws Exception {
		Aluno prestador = cadastrarELogar("prestador");

		mockMvc.perform(post("/servicos")
						.header(HttpHeaders.AUTHORIZATION, bearer(prestador.token()))
						.contentType(MediaType.APPLICATION_JSON)
						.content(payloadServico("Aulas de Java")))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", startsWith("http://localhost/servicos/")))
				.andExpect(jsonPath("$.titulo").value("Aulas de Java"))
				.andExpect(jsonPath("$.categoria").value("Tutoria"))
				.andExpect(jsonPath("$.preco").value(50.00))
				.andExpect(jsonPath("$.situacao").value("ATIVO"))
				.andExpect(jsonPath("$.prestadorId").value(prestador.id().intValue()));
	}

	@Test
	void deveRecusarPublicacaoSemToken() throws Exception {
		mockMvc.perform(post("/servicos")
						.contentType(MediaType.APPLICATION_JSON)
						.content(payloadServico("Aulas de Java")))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("Não autenticado"));
	}

	@Test
	void deveRejeitarPublicacaoInvalida() throws Exception {
		Aluno prestador = cadastrarELogar("invalido");

		mockMvc.perform(post("/servicos")
						.header(HttpHeaders.AUTHORIZATION, bearer(prestador.token()))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"titulo": "",
									"descricao": "",
									"categoria": "",
									"preco": -1
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Dados inválidos"))
				.andExpect(jsonPath("$.fieldErrors").isArray());
	}

	@Test
	void deveListarApenasServicosPublicados() throws Exception {
		Aluno prestador = cadastrarELogar("lista");
		Aluno outro = cadastrarELogar("listaoutro");
		Long ativoId = publicar(prestador, "Serviço visível");
		Long encerradoId = publicar(prestador, "Serviço encerrado");
		encerrar(prestador, encerradoId);

		mockMvc.perform(get("/servicos")
						.header(HttpHeaders.AUTHORIZATION, bearer(outro.token())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].id", hasItem(ativoId.intValue())))
				.andExpect(jsonPath("$[?(@.id == %d)]", encerradoId).isEmpty());
	}

	@Test
	void deveContratarServicoAtivo() throws Exception {
		Aluno prestador = cadastrarELogar("dono");
		Aluno contratante = cadastrarELogar("cliente");
		Long servicoId = publicar(prestador, "Revisão de TCC");

		mockMvc.perform(post("/servicos/{id}/contratacoes", servicoId)
						.header(HttpHeaders.AUTHORIZATION, bearer(contratante.token())))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.servicoId").value(servicoId.intValue()))
				.andExpect(jsonPath("$.contratanteId").value(contratante.id().intValue()))
				.andExpect(jsonPath("$.prestadorId").value(prestador.id().intValue()))
				.andExpect(jsonPath("$.situacao").value("SOLICITADA"));
	}

	@Test
	void deveRecusarContratacaoSemToken() throws Exception {
		Aluno prestador = cadastrarELogar("semtoken");
		Long servicoId = publicar(prestador, "Mentoria");

		mockMvc.perform(post("/servicos/{id}/contratacoes", servicoId))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("Não autenticado"));
	}

	@Test
	void deveRecusarContratarServicoInativo() throws Exception {
		Aluno prestador = cadastrarELogar("inativo");
		Aluno contratante = cadastrarELogar("clienteinativo");
		Long servicoId = publicar(prestador, "Serviço pausado");
		encerrar(prestador, servicoId);

		mockMvc.perform(post("/servicos/{id}/contratacoes", servicoId)
						.header(HttpHeaders.AUTHORIZATION, bearer(contratante.token())))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.message").value("Não é possível contratar um serviço que não está ativo"));
	}

	@Test
	void deveRecusarContratarProprioServico() throws Exception {
		Aluno prestador = cadastrarELogar("proprio");
		Long servicoId = publicar(prestador, "Meu serviço");

		mockMvc.perform(post("/servicos/{id}/contratacoes", servicoId)
						.header(HttpHeaders.AUTHORIZATION, bearer(prestador.token())))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.message").value("Não é possível contratar o próprio serviço"));
	}

	@Test
	void prestadorDeveEncerrarServico() throws Exception {
		Aluno prestador = cadastrarELogar("encerra");
		Long servicoId = publicar(prestador, "Serviço a encerrar");

		mockMvc.perform(patch("/servicos/{id}/encerrar", servicoId)
						.header(HttpHeaders.AUTHORIZATION, bearer(prestador.token())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(servicoId.intValue()))
				.andExpect(jsonPath("$.situacao").value("ENCERRADO"));
	}

	@Test
	void outroUsuarioNaoDeveEncerrarServico() throws Exception {
		Aluno prestador = cadastrarELogar("donoencerra");
		Aluno outro = cadastrarELogar("intruso");
		Long servicoId = publicar(prestador, "Serviço alheio");

		mockMvc.perform(patch("/servicos/{id}/encerrar", servicoId)
						.header(HttpHeaders.AUTHORIZATION, bearer(outro.token())))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.message").value("Apenas o prestador pode encerrar este serviço"));
	}

	@Test
	void deveRecusarEncerrarSemToken() throws Exception {
		Aluno prestador = cadastrarELogar("encerrasemtoken");
		Long servicoId = publicar(prestador, "Serviço");

		mockMvc.perform(patch("/servicos/{id}/encerrar", servicoId))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("Não autenticado"));
	}

	private Aluno cadastrarELogar(String prefixo) throws Exception {
		String email = prefixo + "+" + UUID.randomUUID() + "@fiap.com.br";
		MvcResult criado = mockMvc.perform(post("/usuarios")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"nome": "Aluno FIAP",
									"email": "%s",
									"senha": "senha1234"
								}
								""".formatted(email)))
				.andExpect(status().isCreated())
				.andReturn();
		Number id = JsonPath.read(criado.getResponse().getContentAsString(), "$.id");

		MvcResult login = mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"email": "%s",
									"senha": "senha1234"
								}
								""".formatted(email)))
				.andExpect(status().isOk())
				.andReturn();
		String token = JsonPath.read(login.getResponse().getContentAsString(), "$.token");
		return new Aluno(id.longValue(), token);
	}

	private Long publicar(Aluno prestador, String titulo) throws Exception {
		MvcResult resultado = mockMvc.perform(post("/servicos")
						.header(HttpHeaders.AUTHORIZATION, bearer(prestador.token()))
						.contentType(MediaType.APPLICATION_JSON)
						.content(payloadServico(titulo)))
				.andExpect(status().isCreated())
				.andReturn();
		Number id = JsonPath.read(resultado.getResponse().getContentAsString(), "$.id");
		return id.longValue();
	}

	private void encerrar(Aluno prestador, Long servicoId) throws Exception {
		mockMvc.perform(patch("/servicos/{id}/encerrar", servicoId)
						.header(HttpHeaders.AUTHORIZATION, bearer(prestador.token())))
				.andExpect(status().isOk());
	}

	private String payloadServico(String titulo) {
		return """
				{
					"titulo": "%s",
					"descricao": "Ajuda pontual no campus",
					"categoria": "Tutoria",
					"preco": 50.00
				}
				""".formatted(titulo);
	}

	private String bearer(String token) {
		return "Bearer " + token;
	}

	private record Aluno(Long id, String token) {
	}
}
