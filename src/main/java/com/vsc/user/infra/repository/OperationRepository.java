package com.vsc.user.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vsc.user.infra.dao.OperationEntity;

import jakarta.transaction.Transactional;

public interface OperationRepository extends JpaRepository<OperationEntity, Long> {

	@Transactional
	@Modifying
	@Query(value = "UPDATE dev.operation SET deleted_at = NOW(), is_deleted = true WHERE id = :id", nativeQuery = true)
	void softDelete(@Param("id") Long id);
}
