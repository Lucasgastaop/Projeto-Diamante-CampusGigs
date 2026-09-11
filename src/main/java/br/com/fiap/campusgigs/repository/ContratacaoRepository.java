package br.com.fiap.campusgigs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.fiap.campusgigs.model.Contratacao;

@Repository
public interface ContratacaoRepository extends JpaRepository<Contratacao, Long> {
}
