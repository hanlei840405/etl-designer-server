package com.nxin.framework.etl.designer.converter.bean.base;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.vo.basic.UserVo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class UserConverter extends BeanConverter<UserVo, User> {

    @Override
    public UserVo convert(User user, boolean deep) {
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo, "tenant", "projects");
        return userVo;
    }

    @Override
    public List<UserVo> convert(List<User> users) {
        return users.stream().map(item -> convert(item, false)).collect(Collectors.toList());
    }
}
