package com.nxin.framework.etl.designer.repository.designer;

import com.nxin.framework.etl.designer.entity.designer.Shell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShellRepository extends JpaRepository<Shell, Long> {

    Shell getFirstByIdAndTenantId(Long id, Long tenantId);

    List<Shell> findAllByProjectIdAndStatusAndTenantId(Long projectId, String status, Long tenantId);
}
