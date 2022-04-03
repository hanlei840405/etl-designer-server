package com.nxin.framework.etl.designer.entity.auth;

import com.nxin.framework.etl.designer.entity.BaseEntity;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.entity.basic.User;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "auth_user_privilege")
public class UserPrivilege extends BaseEntity implements Serializable {
    @ManyToOne
    @JoinColumn(name = "privilege_id")
    private Privilege privilege;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
}
