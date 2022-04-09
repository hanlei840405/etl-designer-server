package com.nxin.framework.etl.designer.repository.auth;

import com.nxin.framework.etl.designer.entity.auth.Privilege;
import com.nxin.framework.etl.designer.entity.auth.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findAllByIdIn(List<Long> idList);

    List<Resource> findAllByLevelIn(List<String> levels);

    List<Resource> findAllByTenantIdAndLevelIn(Long tenantId, List<String> levels);

    Resource getFirstByCategoryAndLevel(String category, String level);
}
