package com.nxin.framework.etl.designer.vo.analysis;

import com.nxin.framework.etl.designer.vo.BaseVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class ReportDimensionVo extends BaseVo implements Serializable {

    private String code;
    private String name;
    private String expr;
    private String graph;
    private String anchor;
    private String category;
    private String description;
    private ReportVo model;
}
