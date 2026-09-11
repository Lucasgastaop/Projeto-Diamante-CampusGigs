package br.com.fiap.campusgigs.model;

import java.time.LocalDateTime;

import br.com.fiap.campusgigs.model.enums.SituacaoContratacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contratacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contratacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "servico_id", nullable = false)
	private Servico servico;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "contratante_id", nullable = false)
	private Usuario contratante;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private SituacaoContratacao situacao;

	@Column(name = "criado_em", nullable = false)
	@Builder.Default
	private LocalDateTime criadoEm = LocalDateTime.now();
}
