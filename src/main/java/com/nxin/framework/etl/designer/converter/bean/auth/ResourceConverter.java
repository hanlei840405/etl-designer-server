package com.nxin.framework.etl.designer.converter.bean.auth;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.converter.bean.base.TenantConverter;
import com.nxin.framework.etl.designer.entity.auth.Resource;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.vo.auth.ResourceVo;
import com.nxin.framework.etl.designer.vo.basic.TenantVo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ResourceConverter extends BeanConverter<ResourceVo, Resource> {
    @Override
    public ResourceVo convert(Resource resource, boolean deep) {
        ResourceVo resourceVo = new ResourceVo();
        BeanUtils.copyProperties(resource, resourceVo);
        BeanConverter<TenantVo, Tenant> tenantConverter = new TenantConverter();
        if (resource.getTenant() != null) {
            resourceVo.setTenant(tenantConverter.convert(resource.getTenant(), false));
        }
        return resourceVo;
    }

    @Override
    public List<ResourceVo> convert(List<Resource> resources) {
        return resources.stream().map(item -> convert(item, false)).collect(Collectors.toList());
    }
}