package com.nxin.framework.etl.designer.dto.basic;

import com.nxin.framework.etl.designer.vo.basic.TenantVo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ProjectDto implements Serializable {
    private Long id;
    private String name;
    private String description;
    private String scope;
    private String status;
    private Date createTime;
    private String creator;
    private Date lastUpdateTime;
    private String modifier;
    private int version;
    private TenantVo tenant;
    private List<UserDto> users;
}
