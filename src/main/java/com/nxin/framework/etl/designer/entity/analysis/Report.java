package com.nxin.framework.etl.designer.entity.analysis;

import com.nxin.framework.etl.designer.entity.BaseEntity;
import com.nxin.framework.etl.designer.entity.basic.Project;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "analysis_report")
public class Report extends BaseEntity implements Serializable {

    @Column
    private String code;
    @Column
    private String name;
    @Column(columnDefinition = "TEXT")
    private String script;
    @Column
    private String chart;
    @Column
    private String description;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;
    @OneToMany(mappedBy = "report")
    private List<ReportDimension> reportDimensions = new ArrayList<>();
}
