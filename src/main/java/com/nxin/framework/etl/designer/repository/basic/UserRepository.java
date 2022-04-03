package com.nxin.framework.etl.designer.repository.basic;

import com.nxin.framework.etl.designer.entity.basic.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAllByTenantIdAndNameStartsWith(Long tenantId, String name, Pageable pageable);

    Page<User> findAllByTenantId(Long tenantId, Pageable pageable);

    User getFirstByEmail(String email);

    User getFirstByIdAndTenantId(Long id, Long tenantId);
}
