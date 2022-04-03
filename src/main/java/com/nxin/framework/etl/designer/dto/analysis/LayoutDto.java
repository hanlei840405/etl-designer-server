package com.nxin.framework.etl.designer.dto.analysis;

import com.nxin.framework.etl.designer.vo.BaseVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class LayoutDto extends BaseVo implements Serializable {

    private String code;
    private String name;
    private String resourceCode;
    private String description;
    private String arrange;
}
