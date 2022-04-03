package com.nxin.framework.etl.designer.entity.designer;

import com.nxin.framework.etl.designer.entity.BaseEntity;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "designer_attachment")
public class Attachment extends BaseEntity implements Serializable {
    @Column
    private String stepName;
    @Column
    private String path;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    @ManyToOne
    @JoinColumn(name = "shell_id")
    private Shell shell;
}
