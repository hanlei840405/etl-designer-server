package com.nxin.framework.etl.designer.entity.basic;

import com.nxin.framework.etl.designer.entity.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "basic_project")
public class Project extends BaseEntity implements Serializable {
    @Column
    private String name;
    @Column
    private String description;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "basic_project_user")
    private List<User> users = new ArrayList<>(0);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return getId().equals(project.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
