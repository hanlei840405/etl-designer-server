package com.nxin.framework.etl.designer.entity.analysis;

import com.nxin.framework.etl.designer.entity.BaseEntity;
import com.nxin.framework.etl.designer.entity.basic.Project;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.entity.designer.Datasource;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "analysis_model")
public class Model extends BaseEntity implements Serializable {

    @Column
    private String code;
    @Column
    private String name;
    @Column
    private String description;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
    @ManyToOne
    @JoinColumn(name = "datasource_id")
    private Datasource datasource;
    @OneToMany(mappedBy = "model")
    private List<Metadata> metadataList = new ArrayList<>();
}
