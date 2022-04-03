package com.nxin.framework.etl.designer.service.analysis;

import com.nxin.framework.etl.designer.entity.analysis.Layout;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.repository.analysis.LayoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class LayoutService {
    @Autowired
    private LayoutRepository layoutRepository;
    @Autowired
    private ReportDimensionService reportDimensionService;

    public Layout one(Long id, Long tenantId) {
        return layoutRepository.getFirstByIdAndTenantId(id, tenantId);
    }

    public List<Layout> search(String code, List<String> resourceCodeList, Long tenantId) {
        if (StringUtils.hasLength(code)) {
            return layoutRepository.findAllByCodeStartsWithAndResourceCodeInAndTenantId(code, resourceCodeList, tenantId, Sort.by(Sort.Order.desc("status"), Sort.Order.desc("createTime")));
        } else {
            return layoutRepository.findAllByResourceCodeInAndTenantId(resourceCodeList, tenantId, Sort.by(Sort.Order.desc("status"), Sort.Order.desc("createTime")));
        }
    }

    public List<Layout> show(String code, List<String> resourceCodeList, Long tenantId) {
        if (StringUtils.hasLength(code)) {
            return layoutRepository.findAllByCodeStartsWithAndStatusAndResourceCodeInAndTenantId(code, Constant.ACTIVE, resourceCodeList, tenantId, Sort.by(Sort.Order.desc("status"), Sort.Order.desc("createTime")));
        } else {
            return layoutRepository.findAllByStatusAndResourceCodeInAndTenantId(Constant.ACTIVE, resourceCodeList, tenantId, Sort.by(Sort.Order.desc("status"), Sort.Order.desc("createTime")));
        }
    }

    public Layout save(Layout layout, Tenant tenant) {
        layout.setTenant(tenant);
        layoutRepository.save(layout);
        return layout;
    }

    public Layout delete(Layout layout) {
        layoutRepository.save(layout);
        return layout;
    }
}
