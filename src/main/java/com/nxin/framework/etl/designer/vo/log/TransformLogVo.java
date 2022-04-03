package com.nxin.framework.etl.designer.vo.log;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Builder
@Data
public class TransformLogVo implements Serializable {
    private Integer idBatch;
    private String channelId;
    private String transName;
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