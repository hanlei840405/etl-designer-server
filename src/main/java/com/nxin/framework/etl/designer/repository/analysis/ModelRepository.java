package com.nxin.framework.etl.designer.repository.analysis;

import com.nxin.framework.etl.designer.entity.analysis.Model;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {

    Model getFirstByIdAndTenantId(Long id, Long tenantId);

    List<Model> findAllByProjectIdAndTenantId(Long projectId, Long tenantId, Sort sort);

    List<Model> findAllByProjectIdAndCodeStartsWithAndTenantId(Long projectId, String code, Long tenantId, Sort sort);
}
