package com.nxin.framework.etl.designer.converter.bean.auth;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.converter.bean.base.UserConverter;
import com.nxin.framework.etl.designer.entity.auth.Privilege;
import com.nxin.framework.etl.designer.entity.auth.UserPrivilege;
import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.vo.auth.PrivilegeVo;
import com.nxin.framework.etl.designer.vo.auth.UserPrivilegeVo;
import com.nxin.framework.etl.designer.vo.basic.UserVo;

import java.util.List;
import java.util.stream.Collectors;

public class UserPrivilegeConverter extends BeanConverter<UserPrivilegeVo, UserPrivilege> {

    @Override
    public UserPrivilegeVo convert(UserPrivilege userPrivilege, boolean deep) {
        BeanConverter<UserVo, User> userConverter = new UserConverter();
        BeanConverter<PrivilegeVo, Privilege> privilegeConverter = new PrivilegeConverter();
        UserPrivilegeVo userPrivilegeVo = new UserPrivilegeVo();
        if (userPrivilege.getUser() != null) {
            userPrivilegeVo.setUser(userConverter.convert(userPrivilege.getUser(), false));
        }
        if (userPrivilege.getUser() != null) {
            userPrivilegeVo.setPrivilege(privilegeConverter.convert(userPrivilege.getPrivilege(), false));
        }
        return userPrivilegeVo;
    }

    @Override
    public List<UserPrivilegeVo> convert(List<UserPrivilege> userPrivileges) {
        return userPrivileges.stream().map(item -> convert(item, false)).collect(Collectors.toList());
    }
}