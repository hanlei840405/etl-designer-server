package com.nxin.framework.etl.designer.entity.basic;

import com.nxin.framework.etl.designer.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;


@Data
@Entity
@Table(name = "basic_tenant")
public class Tenant extends BaseEntity implements Serializable {
    @Column
    private String name;
    @Column
    private String logo;
    @Column
    private String domain;
    @Column
    private String telephone;
    @Column
    private String email;
    @Column
    private Date probationEndDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tenant tenant = (Tenant) o;
        return getId().equals(tenant.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
