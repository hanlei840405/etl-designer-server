package com.nxin.framework.etl.designer.dto.basic;

import com.nxin.framework.etl.designer.vo.basic.ProjectVo;
import com.nxin.framework.etl.designer.vo.basic.TenantVo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class UserDto implements Serializable {

    private Long id;
    private boolean master;
    private String name;
    private String mobile;
    private String email;
    private String gender;
    private String wechat;
    private String password;
    private Date birthDate;
    private String status;
    private Date createTime;
    private String creator;
    private Date lastUpdateTime;
    private String modifier;
    private TenantVo tenant;
    private int version;
    private List<ProjectVo> projects;

}
