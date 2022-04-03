package com.nxin.framework.etl.designer.vo.log;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Builder
@Data
public class JobLogVo implements Serializable {
    private Integer idJob;
    private String channelId;
    private String jobName;
    private String status;
    private Long linesRead;
    private Long linesWritten;
    private Long linesUpdated;
    private Long linesInput;
    private Long linesOutput;
    private Long linesRejected;
    private Long errors;
    private Date startDate;
    private Date endDate;
    private Date logDate;
    private Date depDate;
    private Date replayDate;
    private String logField;
}
