package com.nxin.framework.etl.designer.service.analysis;

import com.nxin.framework.etl.designer.entity.analysis.Report;
import com.nxin.framework.etl.designer.entity.analysis.ReportDimension;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.repository.analysis.ReportDimensionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportDimensionService {
    @Autowired
    private ReportDimensionRepository reportDimensionRepository;

    public void save(Report report, List<ReportDimension> reportDimensionList, Tenant tenant) {
        if (!reportDimensionList.isEmpty()) {
            Map<String, ReportDimension> records = reportDimensionList.stream().collect(Collectors.toMap(reportDimension -> reportDimension.getCategory().concat("::").concat(reportDimension.getCode()), reportDimension -> reportDimension, (k1, k2) -> k1, LinkedHashMap::new));
            List<ReportDimension> persisted = reportDimensionRepository.findByReportIdAndStatus(report.getId(), Constant.ACTIVE);
            Map<String, ReportDimension> persistedMap = new LinkedHashMap<>();
            for (ReportDimension dimension : persisted) {
                persistedMap.putIfAbsent(dimension.getCode(), dimension);
            }
            List<ReportDimension> insertOrUpdateList = new ArrayList<>(0);
            persistedMap.keySet().forEach(k -> {
                ReportDimension reportDimension = persistedMap.get(k);
                if (!records.containsKey(k)) { // delete
                    reportDimension.setStatus(Constant.INACTIVE);
                    insertOrUpdateList.add(reportDimension);
                } else { // update
                    BeanUtils.copyProperties(records.get(k), reportDimension, "id", "report", "createTime", "creator");
                    reportDimension.setStatus(Constant.ACTIVE);
                    insertOrUpdateList.add(reportDimension);
                    records.remove(k);
                }
            });
            records.values().forEach(reportDimension -> {
                reportDimension.setTenant(tenant);
                reportDimension.setStatus(Constant.ACTIVE);
                reportDimension.setReport(report);
            });
            for (ReportDimension iou : insertOrUpdateList) {
                iou.setTenant(tenant);
            }
            insertOrUpdateList.addAll(records.values());
            reportDimensionRepository.saveAll(insertOrUpdateList);
        }
    }
}
