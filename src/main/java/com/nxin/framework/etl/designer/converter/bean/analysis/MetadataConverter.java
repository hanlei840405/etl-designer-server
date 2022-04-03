package com.nxin.framework.etl.designer.converter.bean.analysis;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.entity.analysis.Metadata;
import com.nxin.framework.etl.designer.entity.analysis.Model;
import com.nxin.framework.etl.designer.vo.analysis.MetadataVo;
import com.nxin.framework.etl.designer.vo.analysis.ModelVo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class MetadataConverter extends BeanConverter<MetadataVo, Metadata> {

    @Override
    public MetadataVo convert(Metadata metadata, boolean deep) {
        BeanConverter<ModelVo, Model> modelConverter = new ModelConverter();
        MetadataVo modelVo = new MetadataVo();
        BeanUtils.copyProperties(metadata, modelVo, "model");
        if (metadata.getModel() != null) {
            modelVo.setModel(modelConverter.convert(metadata.getModel(), false));
        }
        return modelVo;
    }

    @Override
    public List<MetadataVo> convert(List<Metadata> metadataList) {
        return metadataList.stream().map(item -> convert(item, false)).collect(Collectors.toList());
    }
}
