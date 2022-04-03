package com.nxin.framework.etl.designer.vo.designer;

import com.nxin.framework.etl.designer.vo.BaseVo;
import com.nxin.framework.etl.designer.vo.basic.ProjectVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class ShellVo extends BaseVo implements Serializable {

    private String name;
    private String description;
    private String category;
    private String reference;
    private String content;
    private String streaming;
    private String xml;
    private boolean executable;
    private ShellVo shell;
    private ProjectVo project;
}
