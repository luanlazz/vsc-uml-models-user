package com.vsc.user.infra.dao;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import com.vsc.user.utils.RandomStringGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "version")
public class VersionEntity {

	@Id
	@GeneratedValue(generator = "version_id_sequence", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "version_id_sequence", sequenceName = "version_id_sequence", allocationSize = 1)
	@Column(name = "id")
	private Long id;
	@Column(name = "token")
	private String token;
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_at")
	private Date createDate;

	public VersionEntity() {
		this.token = RandomStringGenerator.generateToken();
	}

	public Long getId() {
		return id;
	}

	public String getToken() {
		return token;
	}
}
