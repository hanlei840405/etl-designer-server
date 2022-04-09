package com.nxin.framework.etl.designer.repository.auth;

import com.nxin.framework.etl.designer.entity.auth.UserPrivilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPrivilegeRepository extends JpaRepository<UserPrivilege, Long> {
    List<UserPrivilege> findAllByUserIdAndTenantIdAndStatus(Long userId, Long tenantId, String status);
}
