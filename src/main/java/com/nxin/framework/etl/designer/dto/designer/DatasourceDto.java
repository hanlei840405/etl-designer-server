package com.nxin.framework.etl.designer.dto.designer;

import com.nxin.framework.etl.designer.dto.basic.ProjectDto;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Builder
@Data
public class DatasourceDto implements Serializable {
    private Long id;
    private String name;
    private String category;
    private String host;
    private Integer port;
    private String schemaName;
    private String username;
    private String password;
    private String dataSpace;
    private String indexSpace;
    private String parameter;
    private Boolean usePool;
    private Boolean useCursor;
    private Integer poolInitialSize;
    private Integer poolMaxSize;
    private Integer poolInitial;
    private Integer poolMaxActive;
    private Integer poolMaxIdle;
    private Integer poolMinIdle;
    private Integer poolMaxWait;
    private Date createTime;
    private String creator;
    private Date modifyTime;
    private String modifier;
    private String status;
    private ProjectDto project;
    private int version;
}
