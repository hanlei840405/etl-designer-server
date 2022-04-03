package com.nxin.framework.etl.designer.entity.designer;

import com.nxin.framework.etl.designer.entity.BaseEntity;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "running_process")
public class RunningProcess extends BaseEntity implements Serializable {
    @Column
    private String instanceId;
    @Column
    private String instanceName;
    @Column
    private String category;
    @Column
    private String prod;
    @Column
    private String owner;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    @ManyToOne
    @JoinColumn(name = "shell_id")
    private Shell shell;
    @ManyToOne
    @JoinColumn(name = "shell_publish_id")
    private ShellPublish shellPublish;
}
