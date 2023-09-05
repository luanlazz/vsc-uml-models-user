package com.vsc.user.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vsc.user.infra.dao.AttributeEntity;

import jakarta.transaction.Transactional;

public interface AttributeRepository extends JpaRepository<AttributeEntity, Long> {

	@Transactional
	@Modifying
	@Query(value = "UPDATE dev.attribute SET deleted_at = NOW(), is_deleted = true WHERE id = :id", nativeQuery = true)
	void softDelete(@Param("id") Long id);
}
