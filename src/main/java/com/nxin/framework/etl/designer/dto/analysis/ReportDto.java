package com.nxin.framework.etl.designer.dto.analysis;

import com.nxin.framework.etl.designer.dto.basic.ProjectDto;
import com.nxin.framework.etl.designer.vo.BaseVo;
import com.nxin.framework.etl.designer.vo.analysis.ModelVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ReportDto extends BaseVo implements Serializable {

    private String code;
    private String name;
    private String script;
    private String chart;
    private String mode;
    private String description;
    private ProjectDto project;
    private ModelVo model;
    private List<ReportDimensionDto> reportDimensions;
}
