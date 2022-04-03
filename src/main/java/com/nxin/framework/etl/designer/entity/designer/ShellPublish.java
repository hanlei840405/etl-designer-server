package com.nxin.framework.etl.designer.entity.designer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nxin.framework.etl.designer.entity.BaseEntity;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "designer_shell_publish")
public class ShellPublish extends BaseEntity implements Serializable {
    @Column
    private String businessId;
    @Column
    private String description;
    @Column
    private String reference;
    @Column
    private String prod;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Column
    private String streaming;
    @Column
    @JsonIgnore
    private String xml;
    @Column
    @JsonIgnore
    private String prodPath;
    @Column
    private String taskId;
    @Column
    private Date deployTime;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    @ManyToOne
    @JoinColumn(name = "shell_id")
    private Shell shell;
    @Transient
    private String cron;
}
