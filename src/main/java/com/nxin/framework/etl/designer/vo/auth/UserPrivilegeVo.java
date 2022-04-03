package com.nxin.framework.etl.designer.vo.auth;

import com.nxin.framework.etl.designer.vo.BaseVo;
import com.nxin.framework.etl.designer.vo.basic.UserVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserPrivilegeVo extends BaseVo implements Serializable {

    private PrivilegeVo privilege;
    private UserVo user;
}
