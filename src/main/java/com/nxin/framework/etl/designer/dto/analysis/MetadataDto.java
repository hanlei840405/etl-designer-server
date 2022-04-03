package com.nxin.framework.etl.designer.dto.analysis;

import com.nxin.framework.etl.designer.vo.BaseVo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class MetadataDto extends BaseVo implements Serializable {

    private Long id;
    private String columnCode;
    private String columnName;
    private String columnCategory;
    private int columnLength;
    private int columnDecimal;
    private boolean primaryKey;
    private boolean notNull;
    private boolean autoIncrement;
    private ModelDto model;
    private String status;
    private Date createTime;
    private Date modifyTime;
    private String creator;
    private String modifier;
    private int version;
}
