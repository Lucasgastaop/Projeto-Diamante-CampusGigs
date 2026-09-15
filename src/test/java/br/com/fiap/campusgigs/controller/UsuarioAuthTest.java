package br.com.fiap.campusgigs.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioAuthTest {

	private static final String JWT_PATTERN = "^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void deveCadastrarUsuarioComSenhaProtegida() throws Exception {
		String email = emailUnico("ana");

		MvcResult resultado = mockMvc.perform(post("/usuarios")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"nome": "Ana Souza",
									"email": "%s",
									"senha": "senha1234"
								}
								""".formatted(email)))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", startsWith("http://localhost/usuarios/")))
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.nome").value("Ana Souza"))
				.andExpect(jsonPath("$.email").value(email))
				.andExpect(jsonPath("$.papel").value("USER"))
				.andExpect(jsonPath("$.senha").doesNotExist())
				.andReturn();

		Number id = JsonPath.read(resultado.getResponse().getContentAsString(), "$.id");
		String hash = jdbcTemplate.queryForObject(
				"SELECT senha FROM usuario WHERE id = ?",
				String.class,
				id.longValue());

		assertThat(hash)
				.startsWith("$2")
				.isNotEqualTo("senha1234");
	}

	@Test
	void naoDeveCadastrarEmailDuplicado() throws Exception {
		String email = emailUnico("duplicado");
		cadastrar(email, "senha1234");

		mockMvc.perform(post("/usuarios")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"nome": "Outra Pessoa",
									"email": "%s",
									"senha": "outrasenha"
								}
								""".formatted(email)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.message").value("E-mail já cadastrado: " + email));
	}

	@Test
	void deveRejeitarCadastroInvalido() throws Exception {
		mockMvc.perform(post("/usuarios")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"nome": "",
									"email": "nao-e-email",
									"senha": "123"
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Dados inválidos"))
				.andExpect(jsonPath("$.fieldErrors").isArray());
	}

	@Test
	void deveAutenticarEDevolverJwt() throws Exception {
		String email = emailUnico("login");
		cadastrar(email, "senha1234");

		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"email": "%s",
									"senha": "senha1234"
								}
								""".formatted(email)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value(matchesPattern(JWT_PATTERN)))
				.andExpect(jsonPath("$.tipo").value("Bearer"))
				.andExpect(jsonPath("$.usuario.email").value(email))
				.andExpect(jsonPath("$.usuario.papel").value("USER"))
				.andExpect(jsonPath("$.usuario.senha").doesNotExist());
	}

	@Test
	void deveRecusarSenhaInvalida() throws Exception {
		String email = emailUnico("errado");
		cadastrar(email, "senha1234");

		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"email": "%s",
									"senha": "nao-e-essa"
								}
								""".formatted(email)))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("E-mail ou senha inválidos"));
	}

	@Test
	void deveBuscarUsuarioPorIdComToken() throws Exception {
		String email = emailUnico("busca");
		MvcResult criado = cadastrar(email, "senha1234");
		Number id = JsonPath.read(criado.getResponse().getContentAsString(), "$.id");
		String token = autenticar(email, "senha1234");

		mockMvc.perform(get("/usuarios/{id}", id.longValue())
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id.intValue()))
				.andExpect(jsonPath("$.email").value(email))
				.andExpect(jsonPath("$.senha").doesNotExist());
	}

	@Test
	void deveRecusarConsultaSemToken() throws Exception {
		mockMvc.perform(get("/usuarios/{id}", 1L))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("Não autenticado"));
	}

	@Test
	void deveRecusarConsultaComTokenInvalido() throws Exception {
		mockMvc.perform(get("/usuarios/{id}", 1L)
						.header(HttpHeaders.AUTHORIZATION, "Bearer token-invalido"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("Não autenticado"));
	}

	@Test
	void deveRetornar404QuandoUsuarioNaoExiste() throws Exception {
		String email = emailUnico("naoexiste");
		cadastrar(email, "senha1234");
		String token = autenticar(email, "senha1234");

		mockMvc.perform(get("/usuarios/{id}", 999_999L)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Usuário não encontrado: 999999"));
	}

	private MvcResult cadastrar(String email, String senha) throws Exception {
		return mockMvc.perform(post("/usuarios")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"nome": "Aluno FIAP",
									"email": "%s",
									"senha": "%s"
								}
								""".formatted(email, senha)))
				.andExpect(status().isCreated())
				.andReturn();
	}

	private String autenticar(String email, String senha) throws Exception {
		MvcResult resultado = mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"email": "%s",
									"senha": "%s"
								}
								""".formatted(email, senha)))
				.andExpect(status().isOk())
				.andReturn();
		return JsonPath.read(resultado.getResponse().getContentAsString(), "$.token");
	}

	private String emailUnico(String prefixo) {
		return prefixo + "+" + UUID.randomUUID() + "@fiap.com.br";
	}
}
