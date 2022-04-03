package com.nxin.framework.etl.designer.vo.analysis;

import com.nxin.framework.etl.designer.vo.BaseVo;
import com.nxin.framework.etl.designer.vo.basic.ProjectVo;
import com.nxin.framework.etl.designer.vo.designer.DatasourceVo;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class ModelVo extends BaseVo implements Serializable {

    private String code;
    private String name;
    private String description;
    private DatasourceVo datasource;
    private ProjectVo project;
    private List<MetadataVo> metadataList = new ArrayList<>(0);
}
