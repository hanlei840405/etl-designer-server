package com.nxin.framework.etl.designer.service.auth;

import com.nxin.framework.etl.designer.entity.auth.Privilege;
import com.nxin.framework.etl.designer.entity.auth.Resource;
import com.nxin.framework.etl.designer.entity.auth.UserPrivilege;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.repository.auth.UserPrivilegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserPrivilegeService {
    @Autowired
    private UserPrivilegeRepository userPrivilegeRepository;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private ResourceService resourceService;

    public List<UserPrivilege> search(Long accountId, Long tenantId) {
        return userPrivilegeRepository.findAllByUserIdAndTenantIdAndStatus(accountId, tenantId, Constant.ACTIVE);
    }

    public List<UserPrivilege> close(List<UserPrivilege> userPrivileges, String modifier, Tenant tenant) {
        userPrivileges.forEach(accountPrivilege -> {
            accountPrivilege.setStatus(Constant.INACTIVE);
            accountPrivilege.setModifier(modifier);
        });
        return save(userPrivileges, tenant);
    }

    public UserPrivilege save(UserPrivilege userPrivilege, Tenant tenant) {
        userPrivilege.setTenant(tenant);
        return userPrivilegeRepository.save(userPrivilege);
    }

    public List<UserPrivilege> save(List<UserPrivilege> userPrivileges, Tenant tenant) {
        userPrivileges.forEach(userPrivilege -> userPrivilege.setTenant(tenant));
        return userPrivilegeRepository.saveAll(userPrivileges);
    }

    @Transactional
    public List<UserPrivilege> grant(List<Long> resources, String operator, User user) {
        // 查找用户之前的授权数据
        List<UserPrivilege> grantedPrivileges = search(user.getId(), user.getTenant().getId());
        // 删除之前授权
        close(grantedPrivileges, operator, user.getTenant());
        // 根据resource查找已经存在的privilege
        List<Privilege> privileges = privilegeService.findByTenantAndResourceIds(user.getTenant().getId(), resources);
        List<Long> persisted = privileges.stream().map(item -> item.getResource().getId()).collect(Collectors.toList());
        List<Long> emptyIdList = resources.stream().filter(id -> !persisted.contains(id)).collect(Collectors.toList());
        Map<Long, Resource> resourceMap = resourceService.findAllByIdIn(emptyIdList).stream().collect(Collectors.toMap(Resource::getId, resource -> resource));
        List<Privilege> newPrivileges = resources.stream().filter(id -> !persisted.contains(id)).collect(Collectors.toList()).stream().map(id -> {
            // 新建Privilege对象
            Privilege privilege = new Privilege();
            privilege.setStatus(Constant.ACTIVE);
            privilege.setTenant(user.getTenant());
            privilege.setCreator(operator);
            privilege.setModifier(operator);
            privilege.setResource(resourceMap.get(id));
            return privilege;
        }).collect(Collectors.toList());
        privileges.addAll(privilegeService.save(newPrivileges));
        return userPrivilegeRepository.saveAll(privileges.stream().map(privilege -> {
            UserPrivilege userPrivilege = new UserPrivilege();
            userPrivilege.setUser(user);
            userPrivilege.setPrivilege(privilege);
            userPrivilege.setStatus(Constant.ACTIVE);
            userPrivilege.setCreator(operator);
            userPrivilege.setTenant(user.getTenant());
            return userPrivilege;
        }).collect(Collectors.toList()));
    }
}
