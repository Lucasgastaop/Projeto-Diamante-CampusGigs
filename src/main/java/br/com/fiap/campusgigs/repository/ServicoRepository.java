package br.com.fiap.campusgigs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.fiap.campusgigs.model.Servico;
import br.com.fiap.campusgigs.model.enums.SituacaoServico;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

	@Query("""
			SELECT s FROM Servico s
			JOIN FETCH s.prestador
			WHERE s.situacao = :situacao
			ORDER BY s.criadoEm DESC
			""")
	List<Servico> findBySituacaoComPrestador(@Param("situacao") SituacaoServico situacao);

	@Query("""
			SELECT s FROM Servico s
			JOIN FETCH s.prestador
			WHERE s.id = :id
			""")
	Optional<Servico> findByIdComPrestador(@Param("id") Long id);
}
