package com.vsc.user.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vsc.user.infra.dao.OperationParameterEntity;

import jakarta.transaction.Transactional;

public interface OperationParameterRepository extends JpaRepository<OperationParameterEntity, Long> {

	@Transactional
	@Modifying
	@Query(value = "UPDATE dev.op_parameter SET deleted_at = NOW(), is_deleted = true WHERE id = :id", nativeQuery = true)
	void softDelete(@Param("id") Long id);
}
