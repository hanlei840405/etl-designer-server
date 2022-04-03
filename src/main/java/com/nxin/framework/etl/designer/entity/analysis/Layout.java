package com.nxin.framework.etl.designer.entity.analysis;

import com.nxin.framework.etl.designer.entity.BaseEntity;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "analysis_layout")
public class Layout extends BaseEntity implements Serializable {

    @Column
    private String code;
    @Column
    private String name;
    @Column(columnDefinition = "varchar(255) default '' comment '资源码'")
    private String resourceCode;
    @Column
    private String description;
    @Column(columnDefinition = "TEXT")
    private String arrange;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
}
