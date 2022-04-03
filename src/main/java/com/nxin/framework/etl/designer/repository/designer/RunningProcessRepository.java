package com.nxin.framework.etl.designer.repository.designer;

import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.entity.designer.RunningProcess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RunningProcessRepository extends JpaRepository<RunningProcess, Long> {
    RunningProcess getFirstByIdAndTenantId(Long id, Long tenantId);

    List<RunningProcess> findAllByInstanceIdAndTenantId(String instanceId, Long tenantId);

    Page<RunningProcess> findByShellProjectUsersIdOrShellPublishShellProjectUsersId(Long userId1, Long userId2, Pageable pageable);
}
