package com.nxin.framework.etl.designer.vo.designer;

import com.nxin.framework.etl.designer.vo.BaseVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class RunningProcessVo extends BaseVo implements Serializable {

    private String instanceId;
    private String instanceName;
    private String category;
    private String prod;
    private String owner;
    private ShellVo shell;
    private ShellPublishVo shellPublish;
}
