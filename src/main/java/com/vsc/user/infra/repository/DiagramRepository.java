package com.vsc.user.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vsc.user.infra.dao.DiagramEntity;

import jakarta.transaction.Transactional;

public interface DiagramRepository extends JpaRepository<DiagramEntity, Long> {

	@Query("SELECT d FROM DiagramEntity d WHERE idUml = ?1")
	DiagramEntity findByUmlId(String umlId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE dev.diagram SET deleted_at = NOW(), is_deleted = true WHERE id = :id", nativeQuery = true)
	void softDelete(@Param("id") Long id);
}
