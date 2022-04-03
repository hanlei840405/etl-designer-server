package com.nxin.framework.etl.designer.repository.basic;

import com.nxin.framework.etl.designer.entity.basic.Project;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project getFirstByIdAndTenantId(Long id, Long tenantId);

    List<Project> findByTenantIdAndUsersId(Long tenantId, Long userId, Sort sort);

    List<Project> findByTenantIdAndUsersIdAndNameStartsWith(Long tenantId, Long userId, String name, Sort sort);
}
