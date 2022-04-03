package com.nxin.framework.etl.designer.repository.designer;

import com.nxin.framework.etl.designer.entity.designer.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    Attachment getFirstByIdAndTenantId(Long id, Long tenantId);

    Attachment getFirstByShellIdAndStepNameAndPathAndStatusAndTenantId(Long shellId, String stepName, String path, String status, Long tenantId);

    List<Attachment> findByShellIdAndStepNameAndStatusAndTenantId(Long shellId, String stepName, String status, Long tenantId);
}
