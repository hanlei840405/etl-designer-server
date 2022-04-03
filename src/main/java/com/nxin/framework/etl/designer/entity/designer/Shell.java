package com.nxin.framework.etl.designer.entity.designer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nxin.framework.etl.designer.entity.BaseEntity;
import com.nxin.framework.etl.designer.entity.basic.Project;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "designer_shell")
public class Shell extends BaseEntity implements Serializable {
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private String category;
    @Column
    private String reference;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Column
    private String streaming;
    @Column
    @JsonIgnore
    private String xml;
    @Column
    private boolean executable = false;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Shell shell;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

}
