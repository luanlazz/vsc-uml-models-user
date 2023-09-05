package com.vsc.user.infra.dao;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "history_change")
public class HistoryChangeEntity {

	@Id
	@GeneratedValue(generator = "change_history_id_sequence", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "change_history_id_sequence", sequenceName = "change_history_id_sequence", allocationSize = 1)
	@Column(name = "id")
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_diagram", nullable = false)
	@JsonIgnore
	private DiagramEntity diagramEntity;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_version", referencedColumnName = "id")
	private VersionEntity version;
	@Column(name = "entity_type")
	private String entityType;
	@Column(name = "id_entity")
	private Long idEntity;
	@Column(name = "change_type")
	private HistoryChangeType changeType;
	@Column(name = "property")
	private String property;
	@Column(name = "value")
	private String value;
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_at")
	private Date createDate;

	public HistoryChangeEntity() {

	}

	public HistoryChangeEntity(DiagramEntity diagramEntity, String entityType, Long idEntity,
			HistoryChangeType changeType, String property, String value, VersionEntity version) {
		this.diagramEntity = diagramEntity;
		this.entityType = entityType;
		this.idEntity = idEntity;
		this.changeType = changeType;
		this.property = property;
		this.value = value;
		this.version = version;
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
	
	public VersionEntity getVersion() {
		return version;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Long getEntity() {
		return idEntity;
	}

	public void setEntity(Long idEntity) {
		this.idEntity = idEntity;
	}

	public HistoryChangeType getChangeType() {
		return changeType;
	}

	public void setOperation(HistoryChangeType type) {
		this.changeType = type;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
