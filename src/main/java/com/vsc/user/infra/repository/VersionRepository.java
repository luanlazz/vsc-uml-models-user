package com.vsc.user.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vsc.user.infra.dao.VersionEntity;

public interface VersionRepository extends JpaRepository<VersionEntity, Long> {
    VersionEntity getByToken(String token);

}
