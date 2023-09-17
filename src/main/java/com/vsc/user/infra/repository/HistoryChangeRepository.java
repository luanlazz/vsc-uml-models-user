package com.vsc.user.infra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.vsc.user.infra.dao.HistoryChangeEntity;
import com.vsc.user.infra.dao.VersionEntity;

public interface HistoryChangeRepository extends JpaRepository<HistoryChangeEntity, Long> {
    List<HistoryChangeEntity> findByVersion(VersionEntity idVersion);
	
    @Query("SELECT hc FROM HistoryChangeEntity hc WHERE (?1 IS NULL OR version >= ?1) AND version <= ?2 ORDER BY id ASC")
    List<HistoryChangeEntity> findVersionRange(VersionEntity startVersion, VersionEntity lastestVersion);
}
