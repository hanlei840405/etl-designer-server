package com.nxin.framework.etl.designer.entity.analysis;

import com.nxin.framework.etl.designer.entity.BaseEntity;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Entity
@Table(name = "analysis_metadata")
public class Metadata extends BaseEntity implements Serializable {
    @Column
    private String columnCode;
    @Column
    private String columnName;
    @Column
    private String columnCategory;
    @Column
    private int columnLength;
    @Column
    private int columnDecimal;
    @Column
    private boolean primaryKey;
    @Column
    private boolean notNull;
    @Column
    private boolean autoIncrement;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;
}
