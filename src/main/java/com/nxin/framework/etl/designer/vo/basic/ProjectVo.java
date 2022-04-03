package com.nxin.framework.etl.designer.vo.basic;

import com.nxin.framework.etl.designer.vo.BaseVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProjectVo extends BaseVo implements Serializable {
    private String name;
    private String description;
    private String scope;
}
