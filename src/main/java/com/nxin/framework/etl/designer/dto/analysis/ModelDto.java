package com.nxin.framework.etl.designer.dto.analysis;

import com.nxin.framework.etl.designer.dto.basic.ProjectDto;
import com.nxin.framework.etl.designer.dto.designer.DatasourceDto;
import com.nxin.framework.etl.designer.vo.BaseVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ModelDto extends BaseVo implements Serializable {

    private String code;
    private String name;
    private String description;
    private DatasourceDto datasource;
    private ProjectDto project;
    private List<MetadataDto> metadataList;
}
