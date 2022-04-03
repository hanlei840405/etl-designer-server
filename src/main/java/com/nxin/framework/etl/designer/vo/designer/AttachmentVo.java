package com.nxin.framework.etl.designer.vo.designer;

import com.nxin.framework.etl.designer.vo.BaseVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class AttachmentVo extends BaseVo implements Serializable {
    private String stepName;
    private String path;
    private ShellVo shell;
}
