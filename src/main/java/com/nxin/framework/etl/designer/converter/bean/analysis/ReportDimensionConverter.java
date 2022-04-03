package com.nxin.framework.etl.designer.converter.bean.analysis;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.entity.analysis.Report;
import com.nxin.framework.etl.designer.entity.analysis.ReportDimension;
import com.nxin.framework.etl.designer.vo.analysis.ReportDimensionVo;
import com.nxin.framework.etl.designer.vo.analysis.ReportVo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ReportDimensionConverter extends BeanConverter<ReportDimensionVo, ReportDimension> {

    @Override
    public ReportDimensionVo convert(ReportDimension reportDimension, boolean deep) {
        BeanConverter<ReportVo, Report> reportConverter = new ReportConverter();
        ReportDimensionVo reportDimensionVo = new ReportDimensionVo();
        BeanUtils.copyProperties(reportDimension, reportDimensionVo, "model");
        if (reportDimension.getReport() != null) {
            reportDimensionVo.setModel(reportConverter.convert(reportDimension.getReport(), false));
        }
        return reportDimensionVo;
    }

    @Override
    public List<ReportDimensionVo> convert(List<ReportDimension> reportDimensionList) {
        return reportDimensionList.stream().map(item -> convert(item, false)).collect(Collectors.toList());
    }
}
