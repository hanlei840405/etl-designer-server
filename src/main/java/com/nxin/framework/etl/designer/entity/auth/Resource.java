package com.nxin.framework.etl.designer.entity.auth;

import com.nxin.framework.etl.designer.entity.BaseEntity;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "auth_resource")
public class Resource extends BaseEntity implements Serializable {
    @Column
    private String code;
    @Column
    private String name;
    @Column
    private String category;
    @Column
    private String level;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
}
