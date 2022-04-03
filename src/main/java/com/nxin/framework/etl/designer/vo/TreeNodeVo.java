package com.nxin.framework.etl.designer.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
public class TreeNodeVo implements Serializable {
    private long id;
    private long parentId;
    private String label;
    private String category;
    private List<TreeNodeVo> children;
}
