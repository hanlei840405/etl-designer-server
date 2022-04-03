package com.nxin.framework.etl.designer.converter.bean.designer;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.entity.designer.Attachment;
import com.nxin.framework.etl.designer.entity.designer.Shell;
import com.nxin.framework.etl.designer.vo.designer.AttachmentVo;
import com.nxin.framework.etl.designer.vo.designer.ShellVo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class AttachmentConverter extends BeanConverter<AttachmentVo, Attachment> {

    @Override
    public AttachmentVo convert(Attachment attachment, boolean deep) {
        BeanConverter<ShellVo, Shell> shellConverter = new ShellConverter();
        AttachmentVo attachmentVo = new AttachmentVo();
        BeanUtils.copyProperties(attachment, attachmentVo, "shell", "shellPublish");
        if (attachment.getShell() != null) {
            attachmentVo.setShell(shellConverter.convert(attachment.getShell(), false));
        }
        return attachmentVo;
    }

    @Override
    public List<AttachmentVo> convert(List<Attachment> attachments) {
        return attachments.stream().map(item -> convert(item, false)).collect(Collectors.toList());
    }
}
