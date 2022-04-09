package com.nxin.framework.etl.designer.repository.auth;

import com.nxin.framework.etl.designer.entity.auth.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    List<Privilege> findByTenantIdAndStatusAndResourceIdIn(Long tenantId, String status, List<Long> resourceIds);

    List<Privilege> findAllByTenantIdAndStatus(Long tenantId, String status);

    Privilege getFirstByTenantIdAndResourceIdAndStatus(Long tenantId, Long resourceId, String status);
}
