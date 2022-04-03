package com.nxin.framework.etl.designer.converter.bean.base;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.entity.basic.Project;
import com.nxin.framework.etl.designer.vo.basic.ProjectVo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectConverter extends BeanConverter<ProjectVo, Project> {

    @Override
    public ProjectVo convert(Project project, boolean deep) {
        ProjectVo projectVo = new ProjectVo();
        BeanUtils.copyProperties(project, projectVo, "users");
        return projectVo;
    }

    @Override
    public List<ProjectVo> convert(List<Project> projects) {
        return projects.stream().map(item -> convert(item, false)).collect(Collectors.toList());
    }
}
