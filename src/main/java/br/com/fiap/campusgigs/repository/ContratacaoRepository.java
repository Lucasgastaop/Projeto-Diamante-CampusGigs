package br.com.fiap.campusgigs.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.fiap.campusgigs.model.Contratacao;
import br.com.fiap.campusgigs.model.enums.SituacaoContratacao;

@Repository
public interface ContratacaoRepository extends JpaRepository<Contratacao, Long> {

	boolean existsByServicoIdAndContratanteIdAndSituacaoIn(
			Long servicoId,
			Long contratanteId,
			Collection<SituacaoContratacao> situacoes);
}
