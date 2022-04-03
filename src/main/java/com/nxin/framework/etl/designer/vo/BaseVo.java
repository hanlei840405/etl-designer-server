package com.nxin.framework.etl.designer.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseVo implements Serializable {
    private Long id;
    private Date createTime;
    private String creator;
    private Date modifyTime;
    private String modifier;
    private String status;
    private int version;
}
