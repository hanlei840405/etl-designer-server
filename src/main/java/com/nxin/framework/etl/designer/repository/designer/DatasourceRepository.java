package com.nxin.framework.etl.designer.repository.designer;

import com.nxin.framework.etl.designer.entity.designer.Datasource;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasourceRepository extends JpaRepository<Datasource, Long> {

    Datasource getFirstByIdAndTenantId(Long id, Long tenantId);

    List<Datasource> findAllByProjectIdAndTenantId(Long branchId, Long tenantId, Sort sort);

    List<Datasource> findAllByProjectIdAndStatusAndTenantId(Long branchId, String status, Long tenantId, Sort sort);
}
