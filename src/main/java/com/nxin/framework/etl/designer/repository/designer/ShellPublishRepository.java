package com.nxin.framework.etl.designer.repository.designer;

import com.nxin.framework.etl.designer.entity.designer.ShellPublish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShellPublishRepository extends JpaRepository<ShellPublish, Long> {
    Page<ShellPublish> findAllByShellIdAndTenantId(Long shellId, Long tenantId, Pageable pageable);
    Page<ShellPublish> findAllByProdAndShellCategoryAndStreamingAndShellProjectIdInAndTenantId(String prod, String category, String stream, long[] projectIds, Long tenantId, Pageable pageable);
    ShellPublish findFirstByProdAndIdNotAndShellIdAndTenantId(String prod, Long id, Long shellId, Long tenantId);
    ShellPublish findFirstByShellIdAndTenantId(Long shellId, Long tenantId, Sort sort);
    List<ShellPublish> findAllByIdInAndTenantId(long[] ids, Long tenantId);
    ShellPublish getFirstByShellIdAndProdAndTenantId(Long shellId, String prod, Long tenantId, Sort sort);
    ShellPublish getFirstByIdAndTenantId(Long id, Long tenantId);
}
