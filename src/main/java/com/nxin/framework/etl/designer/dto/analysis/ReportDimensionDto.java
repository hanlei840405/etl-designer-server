package com.nxin.framework.etl.designer.dto.analysis;

import com.nxin.framework.etl.designer.vo.BaseVo;
import com.nxin.framework.etl.designer.vo.analysis.ReportVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class ReportDimensionDto extends BaseVo implements Serializable {

    private String code;
    private String name;
    private String expr;
    private String graph;
    private String anchor;
    private String category;
    private String description;
    private ReportDto report;
}
