package com.nxin.framework.etl.designer.service.basic;

import com.nxin.framework.etl.designer.entity.basic.Project;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.entity.designer.Datasource;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.repository.basic.ProjectRepository;
import com.nxin.framework.etl.designer.service.designer.DatasourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private DatasourceService datasourceService;

    public Project one(Long id, Long tenantId) {
        return projectRepository.getFirstByIdAndTenantId(id, tenantId);
    }

    public List<Project> search(String name, Long userId, Long tenantId) {
        if (StringUtils.hasLength(name)) {
            return projectRepository.findByTenantIdAndUsersIdAndNameStartsWith(tenantId, userId, name, Sort.by(Sort.Order.desc("status"), Sort.Order.desc("createTime")));
        }
        return projectRepository.findByTenantIdAndUsersId(tenantId, userId, Sort.by(Sort.Order.desc("status"), Sort.Order.desc("createTime")));
    }

    public Project save(Project project, Tenant tenant) {
        project.setTenant(tenant);
        return projectRepository.save(project);
    }

    @Transactional
    public Project delete(Project project) {
        List<Datasource> datasourceList = datasourceService.all(project.getId(), Constant.ACTIVE, project.getTenant().getId());
        datasourceList.forEach(datasource -> {
            datasource.setStatus(Constant.INACTIVE);
            datasource.setModifier(project.getModifier());
        });
        datasourceService.delete(datasourceList);
        project.getUsers().clear();
        projectRepository.save(project);
        return project;
    }
}
