package com.nxin.framework.etl.designer.entity.designer;

import com.nxin.framework.etl.designer.entity.BaseEntity;
import com.nxin.framework.etl.designer.entity.basic.Project;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "designer_datasource")
public class Datasource extends BaseEntity implements Serializable {
    @Column
    private String name;
    @Column
    private String category;
    @Column
    private String host;
    @Column
    private Integer port;
    @Column
    private String schemaName;
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String dataSpace;
    @Column
    private String indexSpace;
    @Column
    private String parameter;
    @Column
    private Boolean usePool;
    @Column
    private Boolean useCursor;
    @Column
    private Integer poolInitialSize;
    @Column
    private Integer poolMaxSize;
    @Column
    private Integer poolInitial;
    @Column
    private Integer poolMaxActive;
    @Column
    private Integer poolMaxIdle;
    @Column
    private Integer poolMinIdle;
    @Column
    private Integer poolMaxWait;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}
