package com.nxin.framework.etl.designer.dto.designer;

import com.nxin.framework.etl.designer.dto.basic.ProjectDto;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ShellDto implements Serializable {

    private Long id;
    private String name;
    private String description;
    private String category;
    private String reference;
    private String content;
    private String streaming;
    private String xml;
    private boolean executable = false;
    private String status;
    private Date createTime = new Date();
    private String creator;
    private Date lastUpdateTime = new Date();
    private String modifier;
    private ShellDto shell;
    private ProjectDto project;
    private int version;

}
