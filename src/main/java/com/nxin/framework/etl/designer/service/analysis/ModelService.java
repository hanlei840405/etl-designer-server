package com.nxin.framework.etl.designer.service.analysis;

import com.nxin.framework.etl.designer.entity.analysis.Metadata;
import com.nxin.framework.etl.designer.entity.analysis.Model;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.repository.analysis.ModelRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ModelService {
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private MetadataService metadataService;

    public Model one(Long id, Long tenantId) {
        return modelRepository.getFirstByIdAndTenantId(id, tenantId);
    }

    public List<Model> search(Long projectId, String code, Long tenantId) {
        if (StringUtils.hasLength(code)) {
            return modelRepository.findAllByProjectIdAndCodeStartsWithAndTenantId(projectId, code, tenantId, Sort.by(Sort.Order.desc("status"), Sort.Order.desc("createTime")));
        } else {
            return modelRepository.findAllByProjectIdAndTenantId(projectId, tenantId, Sort.by(Sort.Order.desc("status"), Sort.Order.desc("createTime")));
        }
    }

    @SneakyThrows
    @Transactional
    public Model save(Model model, List<Metadata> metadataList, Tenant tenant) {
        model.setTenant(tenant);
        modelRepository.save(model);
        metadataService.save(model, metadataList, tenant);
        return model;
    }

    @Transactional
    public Model delete(Model model) {
        modelRepository.save(model);
        return model;
    }
}
