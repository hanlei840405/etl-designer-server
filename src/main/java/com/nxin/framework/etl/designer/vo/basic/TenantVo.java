package com.nxin.framework.etl.designer.vo.basic;

import com.nxin.framework.etl.designer.vo.BaseVo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TenantVo extends BaseVo implements Serializable {

    private String name;
    private String logo;
    private String domain;
    private String telephone;
    private String email;
    private Date probationEndDate;
}
