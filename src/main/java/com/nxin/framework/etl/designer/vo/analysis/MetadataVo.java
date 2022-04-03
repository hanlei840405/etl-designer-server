package com.nxin.framework.etl.designer.vo.analysis;

import com.nxin.framework.etl.designer.vo.BaseVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class MetadataVo extends BaseVo implements Serializable {

    private String columnCode;
    private String columnName;
    private String columnCategory;
    private int columnLength;
    private int columnDecimal;
    private boolean primaryKey;
    private boolean notNull;
    private boolean autoIncrement;
    private ModelVo model;
}
