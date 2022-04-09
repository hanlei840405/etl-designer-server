package com.nxin.framework.etl.designer.service.auth;

import com.nxin.framework.etl.designer.entity.auth.Privilege;
import com.nxin.framework.etl.designer.entity.auth.Resource;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.repository.auth.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ResourceService {

    private static String ROOT = "0";
    private static String APPLICATION = "1";
    private static String BUSINESS = "2";
    private static String CATEGORY_ROOT = "ROOT";

    @Autowired
    private ResourceRepository resourceRepository;

    public List<Resource> all(Long tenantId) {
        List<Resource> resources = resourceRepository.findAllByLevelIn(Arrays.asList(ROOT, APPLICATION));
        resources.addAll(resourceRepository.findAllByTenantIdAndLevelIn(tenantId, Collections.singletonList(BUSINESS)));
        return resources;
    }

    public List<Resource> findAllByIdIn(List<Long> idList) {
        return resourceRepository.findAllByIdIn(idList);
    }

    public Resource root() {
        return resourceRepository.getFirstByCategoryAndLevel(CATEGORY_ROOT, ROOT);
    }

    public Resource save(Resource resource, Tenant tenant) {
        resource.setTenant(tenant);
        return resourceRepository.save(resource);
    }
}
