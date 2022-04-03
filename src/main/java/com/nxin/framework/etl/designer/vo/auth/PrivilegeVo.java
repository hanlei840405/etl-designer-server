package com.nxin.framework.etl.designer.vo.auth;

import com.nxin.framework.etl.designer.vo.BaseVo;
import com.nxin.framework.etl.designer.vo.basic.TenantVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class PrivilegeVo extends BaseVo implements Serializable {
    private TenantVo tenant;
    private ResourceVo resource;
}
