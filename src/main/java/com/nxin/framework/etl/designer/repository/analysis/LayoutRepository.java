package com.nxin.framework.etl.designer.repository.analysis;

import com.nxin.framework.etl.designer.entity.analysis.Layout;
import com.nxin.framework.etl.designer.entity.analysis.Layout;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LayoutRepository extends JpaRepository<Layout, Long> {
    Layout getFirstByIdAndTenantId(Long id, Long tenantId);

    List<Layout> findAllByCodeStartsWithAndStatusAndResourceCodeInAndTenantId(String code, String status, List<String> resourceCodeList, Long tenantId, Sort sort);

    List<Layout> findAllByCodeStartsWithAndResourceCodeInAndTenantId(String code, List<String> resourceCodeList, Long tenantId, Sort sort);

    List<Layout> findAllByStatusAndResourceCodeInAndTenantId(String status, List<String> resourceCodeList, Long tenantId, Sort sort);

    List<Layout> findAllByResourceCodeInAndTenantId(List<String> resourceCodeList, Long tenantId, Sort sort);
}
