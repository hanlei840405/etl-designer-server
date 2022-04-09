package com.nxin.framework.etl.designer.service.auth;

import com.nxin.framework.etl.designer.entity.auth.Privilege;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.repository.auth.PrivilegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrivilegeService {

    @Autowired
    private PrivilegeRepository privilegeRepository;

    public List<Privilege> findByTenantAndResourceIds(Long tenantId, List<Long> resourceIds) {
        return privilegeRepository.findByTenantIdAndStatusAndResourceIdIn(tenantId, Constant.ACTIVE, resourceIds);
    }

    public Privilege one(Long tenantId, Long resourceId) {
        return privilegeRepository.getFirstByTenantIdAndResourceIdAndStatus(tenantId, resourceId, Constant.ACTIVE);
    }

    public List<Privilege> search(Tenant tenant) {
        return privilegeRepository.findAllByTenantIdAndStatus(tenant.getId(), Constant.ACTIVE);
    }

    public Privilege save(Privilege privilege, Tenant tenant) {
        privilege.setTenant(tenant);
        return privilegeRepository.save(privilege);
    }

    public List<Privilege> save(List<Privilege> privileges) {
        return privilegeRepository.saveAll(privileges);
    }
}
