package com.nxin.framework.etl.designer.repository.analysis;

import com.nxin.framework.etl.designer.entity.analysis.Model;
import com.nxin.framework.etl.designer.entity.analysis.Report;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Report getFirstByIdAndTenantId(Long id, Long tenantId);

    List<Report> findAllByCodeStartsWithAndStatusAndTenantId(String code, String status, Long tenantId, Sort sort);

    List<Report> findAllByStatusAndTenantId(String status, Long tenantId, Sort sort);

    List<Report> findAllByCodeStartsWithAndProjectIdAndTenantId(String code, Long projectId, Long tenantId, Sort sort);

    List<Report> findAllByProjectIdAndTenantId(Long projectId, Long tenantId, Sort sort);
}
