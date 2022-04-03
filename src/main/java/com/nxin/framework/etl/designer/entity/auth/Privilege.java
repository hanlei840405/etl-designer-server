package com.nxin.framework.etl.designer.entity.auth;

import com.nxin.framework.etl.designer.entity.BaseEntity;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "auth_privilege")
public class Privilege extends BaseEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;
}
