package com.nxin.framework.etl.designer.converter.bean.auth;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.converter.bean.base.TenantConverter;
import com.nxin.framework.etl.designer.entity.auth.Privilege;
import com.nxin.framework.etl.designer.entity.auth.Resource;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.vo.auth.PrivilegeVo;
import com.nxin.framework.etl.designer.vo.auth.ResourceVo;
import com.nxin.framework.etl.designer.vo.basic.TenantVo;

import java.util.List;
import java.util.stream.Collectors;

public class PrivilegeConverter extends BeanConverter<PrivilegeVo, Privilege> {

    @Override
    public PrivilegeVo convert(Privilege privilege, boolean deep) {
        BeanConverter<TenantVo, Tenant> tenantConverter = new TenantConverter();
        BeanConverter<ResourceVo, Resource> resourceConverter = new ResourceConverter();
        PrivilegeVo privilegeVo = new PrivilegeVo();
        if (privilege.getTenant() != null) {
            privilegeVo.setTenant(tenantConverter.convert(privilege.getTenant(), false));
        }
        if (privilege.getResource() != null) {
            privilegeVo.setResource(resourceConverter.convert(privilege.getResource(), false));
        }
        return privilegeVo;
    }

    @Override
    public List<PrivilegeVo> convert(List<Privilege> privileges) {
        return privileges.stream().map(item -> convert(item, false)).collect(Collectors.toList());
    }
}