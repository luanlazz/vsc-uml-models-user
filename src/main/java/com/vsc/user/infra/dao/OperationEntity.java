package com.vsc.user.infra.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "operation")
@Where(clause = "is_deleted = false")
public class OperationEntity extends UMLElementEntity {
	
	public static final String Classname = "OperationEntity";
	
	@Id
	@GeneratedValue(generator = "operation_id_sequence", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "operation_id_sequence", sequenceName = "operation_id_sequence", allocationSize = 1)
	@Column(name = "id")
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_class", nullable = false)
	@JsonIgnore
	private ClassEntity classEntity;
	private Long idReturn;
	@Column(name = "id_type")
	private Long IdType;
	@Column(name = "visibility")
	private String visibility;
	@Column(name = "is_class")
	private String isClass;
	@OneToMany(mappedBy = "operation", cascade = CascadeType.ALL)
	private List<OperationParameterEntity> parameters;

	public OperationEntity() {
	}

	public OperationEntity(String idUml, ClassEntity classEntity, Long idReturn, String name, Long idType,
			String visibility, String isClass, VersionEntity version) {
		super(idUml, name, version);
		this.classEntity = classEntity;
		this.idReturn = idReturn;
		this.IdType = idType;
		this.visibility = visibility;
		this.isClass = isClass;
		this.parameters = new ArrayList<OperationParameterEntity>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ClassEntity getClassEntity() {
		return classEntity;
	}

	public void setClassEntity(ClassEntity classEntity) {
		this.classEntity = classEntity;
	}

	public Long getIdType() {
		return IdType;
	}

	public void setIdType(Long idType) {
		IdType = idType;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public Long getIdReturn() {
		return idReturn;
	}

	public void setIdReturn(Long idReturn) {
		this.idReturn = idReturn;
	}

	public void addParameter(OperationParameterEntity parameter) {
		this.parameters.add(parameter);
	}

	public boolean removeParameter(OperationParameterEntity parameter) {
		return parameters.remove(parameter);
	}

	public List<OperationParameterEntity> getParameters() {
		return parameters;
	}

	public void setParameters(List<OperationParameterEntity> parameters) {
		this.parameters = parameters;
	}
}
