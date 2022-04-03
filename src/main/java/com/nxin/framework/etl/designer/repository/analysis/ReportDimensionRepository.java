package com.nxin.framework.etl.designer.repository.analysis;

import com.nxin.framework.etl.designer.entity.analysis.ReportDimension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportDimensionRepository extends JpaRepository<ReportDimension, Long> {
    List<ReportDimension> findByReportIdAndStatus(Long reportId, String status);
}
