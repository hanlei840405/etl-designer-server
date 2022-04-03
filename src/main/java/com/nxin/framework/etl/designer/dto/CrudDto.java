package com.nxin.framework.etl.designer.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CrudDto implements Serializable {
    private Long id;
    private String payload;
    private int pageNo;
    private int pageSize;
    private Boolean ignoreStatus;
    private Date begin;
    private Date end;
}
