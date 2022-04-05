package com.nxin.framework.etl.designer.vo.analysis;

import com.nxin.framework.etl.designer.vo.BaseVo;
import com.nxin.framework.etl.designer.vo.basic.ProjectVo;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReportVo extends BaseVo implements Serializable {

    private String code;
    private String name;
    private String script;
    private String chart;
    private String mode;
    private String description;
    private ProjectVo project;
    private ModelVo model;
    private List<ReportDimensionVo> reportDimensions = new ArrayList<>(0);
}
