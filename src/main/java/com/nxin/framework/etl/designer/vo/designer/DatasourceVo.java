package com.nxin.framework.etl.designer.vo.designer;

import com.nxin.framework.etl.designer.vo.BaseVo;
import com.nxin.framework.etl.designer.vo.basic.ProjectVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class DatasourceVo extends BaseVo implements Serializable {
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
    private ProjectVo project;
}
