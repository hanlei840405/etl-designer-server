package com.nxin.framework.etl.designer.service.designer;

import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.entity.designer.Datasource;
import com.nxin.framework.etl.designer.repository.designer.DatasourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatasourceService {
    @Autowired
    private DatasourceRepository datasourceRepository;

    public Datasource one(Long id) {
        return datasourceRepository.getOne(id);
    }

    public Datasource one(Long id, Long tenantId) {
        return datasourceRepository.getFirstByIdAndTenantId(id, tenantId);
    }

    public List<Datasource> all(Long projectId, Long tenantId) {
        return datasourceRepository.findAllByProjectIdAndTenantId(projectId, tenantId, Sort.by(Sort.Order.desc("status"), Sort.Order.desc("createTime")));
    }

    public List<Datasource> all(Long projectId, String status, Long tenantId) {
        return datasourceRepository.findAllByProjectIdAndStatusAndTenantId(projectId, status, tenantId, Sort.by(Sort.Order.desc("status"), Sort.Order.desc("createTime")));
    }

    public Datasource save(Datasource datasource, Tenant tenant) {
        datasource.setTenant(tenant);
        return datasourceRepository.save(datasource);
    }

    public void delete(List<Datasource> datasourceList) {
        datasourceRepository.saveAll(datasourceList);
    }
}
