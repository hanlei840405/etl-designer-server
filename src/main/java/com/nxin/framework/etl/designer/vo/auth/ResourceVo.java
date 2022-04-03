package com.nxin.framework.etl.designer.vo.auth;

import com.nxin.framework.etl.designer.vo.BaseVo;
import com.nxin.framework.etl.designer.vo.basic.TenantVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class ResourceVo extends BaseVo implements Serializable {

    private String code;
    private String name;
    private String category;
    private String level;
    private TenantVo tenant;
}
