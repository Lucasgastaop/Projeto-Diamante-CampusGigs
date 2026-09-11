package br.com.fiap.campusgigs.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.fiap.campusgigs.model.enums.SituacaoServico;
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
@Table(name = "servico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Servico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "prestador_id", nullable = false)
	private Usuario prestador;

	@Column(nullable = false, length = 150)
	private String titulo;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String descricao;

	@Column(nullable = false, length = 80)
	private String categoria;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal preco;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private SituacaoServico situacao;

	@Column(name = "criado_em", nullable = false)
	@Builder.Default
	private LocalDateTime criadoEm = LocalDateTime.now();
}
