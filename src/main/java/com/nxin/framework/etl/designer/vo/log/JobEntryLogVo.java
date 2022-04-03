package com.nxin.framework.etl.designer.vo.log;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Builder
@Data
public class JobEntryLogVo implements Serializable {
    private Integer idBatch;
    private String channelId;
    private Date logDate;
    private String transName;
    private String stepName;
    private Long linesRead;
    private Long linesWritten;
    private Long linesUpdated;
    private Long linesInput;
    private Long linesOutput;
    private Long linesRejected;
    private Long errors;
    private String result;
    private Long nrResultRows;
    private Long nrResultFiles;
}