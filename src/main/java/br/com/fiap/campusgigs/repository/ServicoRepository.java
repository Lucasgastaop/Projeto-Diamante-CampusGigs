package br.com.fiap.campusgigs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.fiap.campusgigs.model.Servico;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
}
