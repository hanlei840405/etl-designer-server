package com.nxin.framework.etl.designer.entity.log;

import com.nxin.framework.etl.designer.entity.designer.ShellPublish;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "shell_publish_log")
public class ShellPublishLog {
    public ShellPublishLog() {
    }

    public ShellPublishLog(String logChannelId, ShellPublish shellPublish) {
        this.logChannelId = logChannelId;
        this.shellPublish = shellPublish;
    }

    @Id
    private String logChannelId;
    @ManyToOne
    @JoinColumn(name = "shell_publish_id")
    private ShellPublish shellPublish;
    @Column
    private Date createTime = new Date();
}
