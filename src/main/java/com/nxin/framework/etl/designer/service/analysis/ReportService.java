package com.nxin.framework.etl.designer.service.analysis;

import com.nxin.framework.etl.designer.entity.analysis.Report;
import com.nxin.framework.etl.designer.entity.analysis.ReportDimension;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.repository.analysis.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private ReportDimensionService reportDimensionService;

    public Report one(Long id, Long tenantId) {
        return reportRepository.getFirstByIdAndTenantId(id, tenantId);
    }

    public List<Report> search(Long projectId, String code, Long tenantId) {
        if (StringUtils.hasLength(code)) {
            return reportRepository.findAllByCodeStartsWithAndProjectIdAndTenantId(code, projectId, tenantId, Sort.by(Sort.Order.desc("status"), Sort.Order.desc("createTime")));
        } else {
            return reportRepository.findAllByProjectIdAndTenantId(projectId, tenantId, Sort.by(Sort.Order.desc("status"), Sort.Order.desc("createTime")));
        }
    }

    public List<Report> show(String code, Long tenantId) {
        if (StringUtils.hasLength(code)) {
            return reportRepository.findAllByCodeStartsWithAndStatusAndTenantId(code, Constant.ACTIVE, tenantId, Sort.by(Sort.Order.desc("status"), Sort.Order.desc("createTime")));
        } else {
            return reportRepository.findAllByStatusAndTenantId(Constant.ACTIVE, tenantId, Sort.by(Sort.Order.desc("status"), Sort.Order.desc("createTime")));
        }
    }

    @Transactional
    public Report save(Report report, List<ReportDimension> reportDimensionList, Tenant tenant) {
        report.setTenant(tenant);
        reportRepository.save(report);
        reportDimensionService.save(report, reportDimensionList, tenant);
        return report;
    }

    @Transactional
    public Report delete(Report report) {
        reportRepository.save(report);
        return report;
    }
}
