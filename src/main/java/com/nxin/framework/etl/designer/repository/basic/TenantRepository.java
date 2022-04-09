package com.nxin.framework.etl.designer.repository.basic;

import com.nxin.framework.etl.designer.entity.basic.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
}
