package com.nxin.framework.etl.designer.service.basic;

import com.nxin.framework.etl.designer.entity.auth.Privilege;
import com.nxin.framework.etl.designer.entity.auth.UserPrivilege;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.repository.basic.UserRepository;
import com.nxin.framework.etl.designer.service.auth.PrivilegeService;
import com.nxin.framework.etl.designer.service.auth.UserPrivilegeService;
import com.nxin.framework.etl.designer.vo.PageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserPrivilegeService userPrivilegeService;
    @Autowired
    private PrivilegeService privilegeService;

    public User one(Long id, Long tenantId) {
        return userRepository.getFirstByIdAndTenantId(id, tenantId);
    }

    public User one(String email) {
        return userRepository.getFirstByEmail(email);
    }

    public PageVo<User> search(Long tenantId, String name, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Order.desc("status"), Sort.Order.desc("createTime")));
        Page<User> pageRecord;
        if (StringUtils.hasLength(name)) {
            pageRecord = userRepository.findAllByTenantIdAndNameStartsWith(tenantId, name, pageable);
        } else {
            pageRecord = userRepository.findAllByTenantId(tenantId, pageable);
        }
        return new PageVo(pageRecord.getTotalElements(), pageRecord.getContent());
    }

    public User lock(User user) {
        user.setStatus(Constant.LOCKED);
        return modify(user);
    }

    @Transactional
    public User close(User user) {
        List<UserPrivilege> userPrivileges = userPrivilegeService.search(user.getId(), user.getTenant().getId());
        userPrivilegeService.close(userPrivileges, user.getModifier(), user.getTenant());
        user.setStatus(Constant.INACTIVE);
        return modify(user);
    }

    @Transactional
    public User create(User user, Tenant tenant, List<Privilege> privileges) {
        user.setTenant(tenant);
        userRepository.save(user);
        List<UserPrivilege> userPrivileges = privileges.stream().map(privilege -> {
            UserPrivilege userPrivilege = new UserPrivilege();
            userPrivilege.setUser(user);
            userPrivilege.setPrivilege(privilege);
            userPrivilege.setStatus(Constant.ACTIVE);
            return userPrivilege;
        }).collect(Collectors.toList());
        userPrivilegeService.save(userPrivileges, tenant);
        return user;
    }

    public User modify(User user) {
        user.setModifyTime(new Date());
        return userRepository.save(user);
    }
}
