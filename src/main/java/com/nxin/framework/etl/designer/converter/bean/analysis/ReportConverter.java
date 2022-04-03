package com.nxin.framework.etl.designer.converter.bean.analysis;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.converter.bean.base.ProjectConverter;
import com.nxin.framework.etl.designer.entity.analysis.Model;
import com.nxin.framework.etl.designer.entity.analysis.Report;
import com.nxin.framework.etl.designer.entity.analysis.ReportDimension;
import com.nxin.framework.etl.designer.entity.basic.Project;
import com.nxin.framework.etl.designer.vo.analysis.ModelVo;
import com.nxin.framework.etl.designer.vo.analysis.ReportDimensionVo;
import com.nxin.framework.etl.designer.vo.analysis.ReportVo;
import com.nxin.framework.etl.designer.vo.basic.ProjectVo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ReportConverter extends BeanConverter<ReportVo, Report> {

    @Override
    public ReportVo convert(Report report, boolean deep) {
        BeanConverter<ProjectVo, Project> projectConverter = new ProjectConverter();
        BeanConverter<ModelVo, Model> modelConverter = new ModelConverter();
        BeanConverter<ReportDimensionVo, ReportDimension> reportDimensionConverter = new ReportDimensionConverter();
        ReportVo reportVo = new ReportVo();
        BeanUtils.copyProperties(report, reportVo, "model", "project");
        if (report.getProject() != null) {
            reportVo.setProject(projectConverter.convert(report.getProject(), false));
        }
        if (report.getModel() != null) {
            reportVo.setModel(modelConverter.convert(report.getModel(), false));
        }
        if (deep) {
            if (report.getReportDimensions() != null) {
                reportVo.setReportDimensions(reportDimensionConverter.convert(report.getReportDimensions()));
            }
        }
        return reportVo;
    }

    @Override
    public List<ReportVo> convert(List<Report> reports) {
        return reports.stream().map(item -> convert(item, false)).collect(Collectors.toList());
    }
}
