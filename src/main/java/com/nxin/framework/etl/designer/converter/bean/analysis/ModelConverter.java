package com.nxin.framework.etl.designer.converter.bean.analysis;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.converter.bean.base.ProjectConverter;
import com.nxin.framework.etl.designer.converter.bean.designer.DatasourceConverter;
import com.nxin.framework.etl.designer.entity.analysis.Metadata;
import com.nxin.framework.etl.designer.entity.analysis.Model;
import com.nxin.framework.etl.designer.entity.basic.Project;
import com.nxin.framework.etl.designer.entity.designer.Datasource;
import com.nxin.framework.etl.designer.vo.analysis.MetadataVo;
import com.nxin.framework.etl.designer.vo.analysis.ModelVo;
import com.nxin.framework.etl.designer.vo.basic.ProjectVo;
import com.nxin.framework.etl.designer.vo.designer.DatasourceVo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ModelConverter extends BeanConverter<ModelVo, Model> {

    @Override
    public ModelVo convert(Model model, boolean deep) {
        BeanConverter<ProjectVo, Project> projectConverter = new ProjectConverter();
        BeanConverter<DatasourceVo, Datasource> datasourceConverter = new DatasourceConverter();
        BeanConverter<MetadataVo, Metadata> metadataConverter = new MetadataConverter();
        ModelVo modelVo = new ModelVo();
        BeanUtils.copyProperties(model, modelVo, "project", "datasource");
        if (model.getProject() != null) {
            modelVo.setProject(projectConverter.convert(model.getProject(), false));
        }
        if (model.getDatasource() != null) {
            modelVo.setDatasource(datasourceConverter.convert(model.getDatasource(), false));
        }
        if (deep) {
            if (model.getMetadataList() != null) {
                modelVo.setMetadataList(metadataConverter.convert(model.getMetadataList()));
            }
        }
        return modelVo;
    }

    @Override
    public List<ModelVo> convert(List<Model> models) {
        return models.stream().map(item -> convert(item, false)).collect(Collectors.toList());
    }
}
