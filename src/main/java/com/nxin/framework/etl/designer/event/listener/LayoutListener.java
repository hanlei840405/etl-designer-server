package com.nxin.framework.etl.designer.event.listener;

import com.nxin.framework.etl.designer.entity.analysis.Layout;
import com.nxin.framework.etl.designer.entity.auth.Privilege;
import com.nxin.framework.etl.designer.entity.auth.Resource;
import com.nxin.framework.etl.designer.entity.auth.UserPrivilege;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.event.LayoutEvent;
import com.nxin.framework.etl.designer.service.auth.PrivilegeService;
import com.nxin.framework.etl.designer.service.auth.ResourceService;
import com.nxin.framework.etl.designer.service.auth.UserPrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class LayoutListener {
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private UserPrivilegeService userPrivilegeService;

    @EventListener(LayoutEvent.class)
    public void action(LayoutEvent layoutEvent) {
        Layout persisted = (Layout) layoutEvent.getSource();
        Resource resource = new Resource();
        resource.setStatus(Constant.ACTIVE);
        resource.setCode(persisted.getResourceCode());
        resource.setName(persisted.getName());
        resource.setCreator(persisted.getCreator());
        resource.setCreateTime(new Date());
        resource.setModifier(persisted.getCreator());
        resource.setModifyTime(new Date());
        Privilege privilege = new Privilege();
        privilege.setTenant(layoutEvent.getUser().getTenant());
        privilege.setResource(resourceService.save(resource, persisted.getTenant()));
        privilege.setCreator(persisted.getCreator());
        privilege.setCreateTime(new Date());
        privilege.setModifier(persisted.getCreator());
        privilege.setModifyTime(new Date());
        privilegeService.save(privilege, persisted.getTenant());
        UserPrivilege userPrivilege = new UserPrivilege();
        userPrivilege.setPrivilege(privilege);
        userPrivilege.setUser(layoutEvent.getUser());
        userPrivilege.setStatus(Constant.ACTIVE);
        userPrivilege.setCreator(persisted.getCreator());
        userPrivilege.setCreateTime(new Date());
        userPrivilege.setModifier(persisted.getCreator());
        userPrivilege.setModifyTime(new Date());
        userPrivilegeService.save(userPrivilege, persisted.getTenant());
    }
}
