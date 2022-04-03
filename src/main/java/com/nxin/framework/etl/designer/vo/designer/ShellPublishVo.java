package com.nxin.framework.etl.designer.vo.designer;

import com.nxin.framework.etl.designer.vo.BaseVo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ShellPublishVo extends BaseVo implements Serializable {

    private String businessId;
    private String description;
    private String reference;
    private String prod;
    private String content;
    private String streaming;
    private String taskId;
    private Date deployTime;
    private ShellVo shell;
    private String cron;
}
