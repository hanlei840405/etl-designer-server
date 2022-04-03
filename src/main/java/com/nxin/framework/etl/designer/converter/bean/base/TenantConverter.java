package com.nxin.framework.etl.designer.converter.bean.base;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.vo.basic.TenantVo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class TenantConverter extends BeanConverter<TenantVo, Tenant> {
    @Override
    public TenantVo convert(Tenant tenant, boolean deep) {
        TenantVo tenantVo = new TenantVo();
        BeanUtils.copyProperties(tenant, tenantVo);
        return tenantVo;
    }

    @Override
    public List<TenantVo> convert(List<Tenant> tenants) {
        return tenants.stream().map(item -> convert(item, false)).collect(Collectors.toList());
    }
}
