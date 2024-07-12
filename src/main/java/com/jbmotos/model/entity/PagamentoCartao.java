package com.jbmotos.model.entity;

import jakarta.persistence.*;

import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "pagamento_cartao", schema = "jbmotos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class PagamentoCartao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String parcela;

	private String bandeira;

	private BigDecimal totalTaxas;

	@OneToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE })
	@JoinColumn(name = "id_venda", referencedColumnName = "id")
	private Venda venda;
}
