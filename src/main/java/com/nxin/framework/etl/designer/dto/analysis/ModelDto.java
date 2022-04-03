package com.nxin.framework.etl.designer.dto.analysis;

import com.nxin.framework.etl.designer.dto.basic.ProjectDto;
import com.nxin.framework.etl.designer.dto.designer.DatasourceDto;
import com.nxin.framework.etl.designer.vo.BaseVo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ModelDto extends BaseVo implements Serializable {

    private Long id;
    private String code;
    private String name;
    private String status;
    private Date createTime;
    private Date modifyTime;
    private String creator;
    private String modifier;
    private DatasourceDto datasource;
    private ProjectDto project;
    private List<MetadataDto> metadataList;
    private int version;
}
