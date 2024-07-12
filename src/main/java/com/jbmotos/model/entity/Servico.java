package com.jbmotos.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "servico", schema = "jbmotos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Servico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "cpf_funcionario", referencedColumnName = "cpf")
	private Funcionario funcionario;

	@ManyToOne
	@JoinColumn(name = "id_moto", referencedColumnName = "id")
	private Moto moto;

	@OneToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE })
	@JoinColumn(name = "id_venda", referencedColumnName = "id")
	private Venda venda;

	@CreationTimestamp
	private LocalDateTime dataHoraCadastro;

	@Column(length = 300)
	private String servicosRealizados;

	@Column(length = 300)
	private String observacao;

	private BigDecimal precoMaoDeObra;
}
