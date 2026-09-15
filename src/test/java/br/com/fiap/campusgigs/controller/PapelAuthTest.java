package br.com.fiap.campusgigs.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import br.com.fiap.campusgigs.security.AdminUserInitializer;

@SpringBootTest
@AutoConfigureMockMvc
class PapelAuthTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void adminDeveListarUsuarios() throws Exception {
		String token = autenticar(AdminUserInitializer.ADMIN_EMAIL, AdminUserInitializer.ADMIN_SENHA);

		mockMvc.perform(get("/usuarios")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].email").exists())
				.andExpect(jsonPath("$[0].senha").doesNotExist())
				.andExpect(jsonPath("$[?(@.email == '%s')]", AdminUserInitializer.ADMIN_EMAIL).exists());
	}

	@Test
	void userNaoDeveListarUsuarios() throws Exception {
		String email = emailUnico("aluno");
		cadastrar(email, "senha1234");
		String token = autenticar(email, "senha1234");

		mockMvc.perform(get("/usuarios")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.message").value("Acesso negado"));
	}

	@Test
	void userAindaConsultaPorId() throws Exception {
		String email = emailUnico("consulta");
		MvcResult criado = cadastrar(email, "senha1234");
		Number id = JsonPath.read(criado.getResponse().getContentAsString(), "$.id");
		String token = autenticar(email, "senha1234");

		mockMvc.perform(get("/usuarios/{id}", id.longValue())
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value(email))
				.andExpect(jsonPath("$.papel").value("USER"));
	}

	@Test
	void loginAdminDevolvePapelAdmin() throws Exception {
		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"email": "%s",
									"senha": "%s"
								}
								""".formatted(AdminUserInitializer.ADMIN_EMAIL, AdminUserInitializer.ADMIN_SENHA)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.usuario.email").value(AdminUserInitializer.ADMIN_EMAIL))
				.andExpect(jsonPath("$.usuario.papel").value("ADMIN"))
				.andExpect(jsonPath("$.usuario.senha").doesNotExist());
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
