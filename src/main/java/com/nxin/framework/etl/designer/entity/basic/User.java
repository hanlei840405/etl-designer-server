package com.nxin.framework.etl.designer.entity.basic;

import com.nxin.framework.etl.designer.entity.BaseEntity;
import com.nxin.framework.etl.designer.entity.auth.UserPrivilege;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Data
@Entity
@Table(name = "basic_user")
public class User extends BaseEntity implements Serializable {
    @Column
    private boolean master;
    @Column
    private String name;
    @Column
    private String mobile;
    @Column
    private String email;
    @Column
    private String gender;
    @Column
    private String wechat;
    @Column
    private String password;
    @Column
    private Date birthDate;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private List<Project> projects = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private List<UserPrivilege> userPrivileges = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getId().equals(user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
