package com.nxin.framework.etl.designer.service.designer;

import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.entity.designer.Attachment;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.repository.designer.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttachmentService {
    @Autowired
    private AttachmentRepository attachmentRepository;

    public Attachment one(Long id, Long tenantId) {
        return attachmentRepository.getFirstByIdAndTenantId(id, tenantId);
    }

    public Attachment one(Long shellId, Long tenantId, String stepName, String path) {
        return attachmentRepository.getFirstByShellIdAndStepNameAndPathAndStatusAndTenantId(shellId, stepName, path, Constant.ACTIVE, tenantId);
    }

    public List<Attachment> all(Long shellId, Long tenantId, String stepName) {
        return attachmentRepository.findByShellIdAndStepNameAndStatusAndTenantId(shellId, stepName, Constant.ACTIVE, tenantId);
    }

    public Attachment save(Attachment attachment, Tenant tenant) {
        attachment.setTenant(tenant);
        attachmentRepository.save(attachment);
        return attachment;
    }

    public List<Attachment> save(List<Attachment> attachments, Tenant tenant) {
        attachments.forEach(attachment -> attachment.setTenant(tenant));
        return attachmentRepository.saveAll(attachments);
    }
}
