package com.nxin.framework.etl.designer.repository.log;

import com.nxin.framework.etl.designer.entity.log.ShellPublishLog;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShellPublishLogRepository extends JpaRepository<ShellPublishLog, Long> {
    List<ShellPublishLog> findByShellPublishId(Long shellPublishId);
    void deleteByShellPublishId(Long shellPublishId);
}
