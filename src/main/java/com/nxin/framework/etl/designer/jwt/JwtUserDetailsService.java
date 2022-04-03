package com.nxin.framework.etl.designer.jwt;

import com.nxin.framework.etl.designer.entity.auth.Privilege;
import com.nxin.framework.etl.designer.entity.auth.Resource;
import com.nxin.framework.etl.designer.entity.auth.UserPrivilege;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.exception.UsernameExistedException;
import com.nxin.framework.etl.designer.service.auth.PrivilegeService;
import com.nxin.framework.etl.designer.service.auth.ResourceService;
import com.nxin.framework.etl.designer.service.auth.UserPrivilegeService;
import com.nxin.framework.etl.designer.service.basic.TenantService;
import com.nxin.framework.etl.designer.service.basic.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserPrivilegeService userPrivilegeService;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private ResourceService resourceService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        com.nxin.framework.etl.designer.entity.basic.User user = userService.one(s);
        if (user != null) {
            List<UserPrivilege> userPrivileges = userPrivilegeService.search(user.getId(), user.getTenant().getId());
            List<String> authorities = userPrivileges.stream().filter(userPrivilege -> userPrivilege.getPrivilege().getResource() != null).map(userPrivilege -> userPrivilege.getPrivilege().getResource().getCode()).collect(Collectors.toList());
            return new User(s, user.getPassword(), AuthorityUtils.createAuthorityList(authorities.toArray(new String[]{})));
        } else {
            throw new UsernameNotFoundException("User not found with username: " + s);
        }
    }

    @Transactional(rollbackOn = {UsernameExistedException.class})
    public com.nxin.framework.etl.designer.entity.basic.User register(RegisterForm form) throws UsernameExistedException {
        Tenant tenant = new Tenant();
        tenant.setName(form.getCompany());
        tenantService.register(tenant);
        com.nxin.framework.etl.designer.entity.basic.User user = new com.nxin.framework.etl.designer.entity.basic.User();
        BeanUtils.copyProperties(form, user);
        user.setMaster(true);
        user.setTenant(tenant);
        user.setStatus(Constant.ACTIVE);
        user.setPassword(bCryptPasswordEncoder.encode(form.getPassword()));
        Resource resource = resourceService.root();
        Privilege root = new Privilege();
        root.setStatus(Constant.ACTIVE);
        root.setTenant(tenant);
        root.setCreator(user.getEmail());
        root.setModifier(user.getEmail());
        root.setResource(resource);
        privilegeService.save(root, tenant);
        return userService.create(user, tenant, Collections.singletonList(root));
    }
}
