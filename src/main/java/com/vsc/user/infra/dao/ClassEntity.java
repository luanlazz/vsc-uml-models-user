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
@Table(name = "class")
@Where(clause = "is_deleted = false")
public class ClassEntity extends UMLElementEntity {

	public static final String Classname = "ClassEntity";

	@Id
	@GeneratedValue(generator = "class_id_sequence", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "class_id_sequence", sequenceName = "class_id_sequence", allocationSize = 1)
	@Column(name = "id")
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_diagram", nullable = false)
	@JsonIgnore
	private DiagramEntity diagramEntity;
	@OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL)
	private List<AttributeEntity> attributes;
	@OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL)
	private List<OperationEntity> operations;

	public ClassEntity() {
	}

	public ClassEntity(String idUml, DiagramEntity diagramEntity, String name, VersionEntity version) {
		super(idUml, name, version);
		this.diagramEntity = diagramEntity;
		this.attributes = new ArrayList<AttributeEntity>();
		this.operations = new ArrayList<OperationEntity>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DiagramEntity getDiagramEntity() {
		return diagramEntity;
	}

	public void setDiagramEntity(DiagramEntity diagramEntity) {
		this.diagramEntity = diagramEntity;
	}

	public List<AttributeEntity> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<AttributeEntity> attributes) {
		this.attributes = attributes;
	}

	public List<OperationEntity> getOperations() {
		return operations;
	}

	public void setOperations(List<OperationEntity> operations) {
		this.operations = operations;
	}

	public void addAttribute(AttributeEntity attribute) {
		this.attributes.add(attribute);
	}

	public boolean removeAttribute(AttributeEntity attribute) {
		return this.attributes.remove(attribute);
	}

	public void addOperation(OperationEntity operation) {
		this.operations.add(operation);
	}

	public boolean removeOperation(OperationEntity operation) {
		return this.operations.remove(operation);
	}
}
