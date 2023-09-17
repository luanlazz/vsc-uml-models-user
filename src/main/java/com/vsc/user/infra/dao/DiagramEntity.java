package com.vsc.user.infra.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Where;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "diagram")
@Where(clause = "is_deleted = false")
public class DiagramEntity extends UMLElementEntity {
	
	public static final String Classname = "DiagramEntity";

	@Id
	@GeneratedValue(generator = "diagram_id_sequence", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "diagram_id_sequence", sequenceName = "diagram_id_sequence", allocationSize = 1)
	@Column(name = "id")
	private Long id;
	@Column(name = "type")
	private String type;
	@OneToMany(mappedBy = "diagramEntity", cascade = CascadeType.ALL)
	private List<ClassEntity> classes;

	public DiagramEntity() {
	}

	public DiagramEntity(String idUml, String name, String type, VersionEntity version) {
		super(idUml, name, version);
		this.type = type;
		this.classes = new ArrayList<ClassEntity>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void addClass(ClassEntity classEntity) {
		this.classes.add(classEntity);
	}

	public boolean removeClass(ClassEntity classEntity) {
		return this.classes.remove(classEntity);
	}

	public List<ClassEntity> getClasses() {
		return classes;
	}

	public void setClasses(List<ClassEntity> classes) {
		this.classes = classes;
	}
}
