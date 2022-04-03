package com.nxin.framework.etl.designer.converter.bean.analysis;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.entity.analysis.Layout;
import com.nxin.framework.etl.designer.vo.analysis.LayoutVo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class LayoutConverter extends BeanConverter<LayoutVo, Layout> {

    @Override
    public LayoutVo convert(Layout layout, boolean deep) {
        LayoutVo layoutVo = new LayoutVo();
        BeanUtils.copyProperties(layout, layoutVo);
        return layoutVo;
    }

    @Override
    public List<LayoutVo> convert(List<Layout> layouts) {
        return layouts.stream().map(item -> convert(item, false)).collect(Collectors.toList());
    }
}
