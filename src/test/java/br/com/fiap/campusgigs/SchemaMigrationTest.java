package br.com.fiap.campusgigs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class SchemaMigrationTest {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void flywayDeveCriarTabelasIniciais() {
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM usuario", Integer.class)).isNotNull();
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM servico", Integer.class)).isNotNull();
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM contratacao", Integer.class)).isNotNull();
	}
}
