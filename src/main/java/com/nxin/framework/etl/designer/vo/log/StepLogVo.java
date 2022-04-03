package com.nxin.framework.etl.designer.vo.log;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class StepLogVo implements Serializable {
    private String name;
    private String logDate;
    private Integer stepCopy;
    private Long read;
    private Long written;
    private Long updated;
    private Long input;
    private Long output;
    private Long rejected;
    private Long errors;
}