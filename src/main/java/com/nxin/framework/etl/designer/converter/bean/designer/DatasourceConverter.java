package com.nxin.framework.etl.designer.converter.bean.designer;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.converter.bean.base.ProjectConverter;
import com.nxin.framework.etl.designer.entity.basic.Project;
import com.nxin.framework.etl.designer.entity.designer.Datasource;
import com.nxin.framework.etl.designer.vo.basic.ProjectVo;
import com.nxin.framework.etl.designer.vo.designer.DatasourceVo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class DatasourceConverter extends BeanConverter<DatasourceVo, Datasource> {

    @Override
    public DatasourceVo convert(Datasource shell, boolean deep) {
        BeanConverter<ProjectVo, Project> projectConverter = new ProjectConverter();
        DatasourceVo datasourceVo = new DatasourceVo();
        BeanUtils.copyProperties(shell, datasourceVo, "project");
        if (shell.getProject() != null) {
            datasourceVo.setProject(projectConverter.convert(shell.getProject(), false));
        }
        return datasourceVo;
    }

    @Override
    public List<DatasourceVo> convert(List<Datasource> datasourceList) {
        return datasourceList.stream().map(item -> convert(item, false)).collect(Collectors.toList());
    }
}
