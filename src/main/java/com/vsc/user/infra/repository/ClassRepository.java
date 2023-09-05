package com.vsc.user.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vsc.user.infra.dao.ClassEntity;

import jakarta.transaction.Transactional;

public interface ClassRepository extends JpaRepository<ClassEntity, Long> {

	@Query("SELECT c FROM ClassEntity c WHERE idUml = ?1")
	ClassEntity findByUmlId(String umlId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE dev.class SET deleted_at = NOW(), is_deleted = true WHERE id = :id", nativeQuery = true)
	void softDelete(@Param("id") Long id);
}
