package com.vsc.user.infra.dao;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "op_parameter")
@Where(clause = "is_deleted = false")
public class OperationParameterEntity extends UMLElementEntity {

	@Id
	@GeneratedValue(generator = "op_parameter_id_sequence", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "op_parameter_id_sequence", sequenceName = "op_parameter_id_sequence", allocationSize = 1)
	@Column(name = "id")
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_operation", nullable = false)
	@JsonIgnore
	private OperationEntity operation;
	@Column(name = "id_type")
	private Long IdType;
	@Column(name = "value_default")
	private String valueDefault;

	public OperationParameterEntity() {
	}

	public OperationParameterEntity(String idUml, OperationEntity operation, String name, Long idType,
			String valueDefault, VersionEntity version) {
		super(idUml, name, version);
		this.operation = operation;
		this.IdType = idType;
		this.valueDefault = valueDefault;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OperationEntity getOperationEntity() {
		return operation;
	}

	public void setOperationEntity(OperationEntity operation) {
		this.operation = operation;
	}

	public Long getIdType() {
		return IdType;
	}

	public void setIdType(Long idType) {
		IdType = idType;
	}

	public String getValueDefault() {
		return valueDefault;
	}

	public void setValueDefault(String valueDefault) {
		this.valueDefault = valueDefault;
	}
}
