package com.nxin.framework.etl.designer.converter.bean.designer;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.entity.designer.Shell;
import com.nxin.framework.etl.designer.entity.designer.ShellPublish;
import com.nxin.framework.etl.designer.vo.designer.ShellPublishVo;
import com.nxin.framework.etl.designer.vo.designer.ShellVo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ShellPublishConverter extends BeanConverter<ShellPublishVo, ShellPublish> {

    @Override
    public ShellPublishVo convert(ShellPublish shellPublish, boolean deep) {
        BeanConverter<ShellVo, Shell> shellConverter = new ShellConverter();
        ShellPublishVo shellPublishVo = new ShellPublishVo();
        BeanUtils.copyProperties(shellPublish, shellPublishVo, "shell");
        if (shellPublish.getShell() != null) {
            shellPublishVo.setShell(shellConverter.convert(shellPublish.getShell(), false));
        }
        return shellPublishVo;
    }

    @Override
    public List<ShellPublishVo> convert(List<ShellPublish> shellPublishes) {
        return shellPublishes.stream().map(item -> convert(item, false)).collect(Collectors.toList());
    }
}
