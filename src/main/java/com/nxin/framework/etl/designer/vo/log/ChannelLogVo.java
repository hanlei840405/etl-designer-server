package com.nxin.framework.etl.designer.vo.log;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Builder
@Data
public class ChannelLogVo implements Serializable {
    private Integer idBatch;
    private String channelId;
    private String loggingObjectType;
    private String objectName;
    private String objectCopy;
    private String repositoryDirectory;
    private String filename;
    private String objectId;
    private String objectRevision;
    private String parentChannelId;
    private String rootChannelId;
    private Date logDate;
}