package com.nxin.framework.etl.designer.entity.analysis;

import com.nxin.framework.etl.designer.entity.BaseEntity;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "analysis_report_dimension")
public class ReportDimension extends BaseEntity implements Serializable {

    @Column
    private String code;
    @Column
    private String name;
    @Column
    private String graph;
    @Column
    private String anchor;
    @Column
    private String category;
    @Column
    private String description;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;
}
