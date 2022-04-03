package com.nxin.framework.etl.designer.converter.bean.designer;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.converter.bean.base.ProjectConverter;
import com.nxin.framework.etl.designer.entity.basic.Project;
import com.nxin.framework.etl.designer.entity.designer.Shell;
import com.nxin.framework.etl.designer.vo.basic.ProjectVo;
import com.nxin.framework.etl.designer.vo.designer.ShellVo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ShellConverter extends BeanConverter<ShellVo, Shell> {

    @Override
    public ShellVo convert(Shell shell, boolean deep) {
        BeanConverter<ProjectVo, Project> projectConverter = new ProjectConverter();
        ShellVo shellVo = new ShellVo();
        BeanUtils.copyProperties(shell, shellVo, "shell", "project");
        if (shell.getShell() != null) {
            shellVo.setShell(this.convert(shell.getShell(), false));
        }
        if (shell.getProject() != null) {
            shellVo.setProject(projectConverter.convert(shell.getProject(), false));
        }
        return shellVo;
    }

    @Override
    public List<ShellVo> convert(List<Shell> shells) {
        return shells.stream().map(item -> convert(item, false)).collect(Collectors.toList());
    }
}
