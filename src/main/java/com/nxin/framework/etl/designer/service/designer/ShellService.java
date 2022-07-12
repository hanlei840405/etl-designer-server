package com.nxin.framework.etl.designer.service.designer;

import com.google.common.io.Files;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.entity.designer.Shell;
import com.nxin.framework.etl.designer.entity.designer.ShellPublish;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.repository.designer.ShellRepository;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ShellService {
    @Autowired
    private ShellRepository shellRepository;
    @Autowired
    private EtlGeneratorService etlGeneratorService;
    @Value("${dev.dir}")
    private String devDir;

    public Shell one(Long id) {
        return shellRepository.getOne(id);
    }

    public Shell one(Long id, Long tenantId) {
        return shellRepository.getFirstByIdAndTenantId(id, tenantId);
    }

    public List<Shell> all(Long projectId, Long tenantId) {
        return shellRepository.findAllByProjectIdAndStatusAndTenantId(projectId, Constant.ACTIVE, tenantId);
    }

    @Transactional
    public Shell save(Shell shell, Tenant tenant) {
        shell.setTenant(tenant);
        if (Constant.ACTIVE.equals(shell.getStatus()) && StringUtils.hasLength(shell.getContent())) {
            shell.setStreaming(Constant.BATCH);
            if (Constant.TRANSFORM.equals(shell.getCategory())) {
                try {
                    Map<String, Object> transResult = etlGeneratorService.getTransMeta(shell, tenant.getId(), false);
                    TransMeta transMeta = (TransMeta) transResult.get("transMeta");
                    StepMeta[] stepMetas = transMeta.getStepsArray();
                    for (StepMeta stepMeta : stepMetas) {
                        if (Constant.STREAMING_STEP.contains(stepMeta.getTypeId())) {
                            shell.setStreaming(Constant.STREAMING);
                            break;
                        }
                    }
                    String xml = transMeta.getXML();
                    String path = devDir + tenant.getId() + File.separator + shell.getProject().getId() + File.separator + shell.getShell().getId();
                    File folder = new File(path);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    File transFile = new File(path + File.separator + shell.getName() + ".ktr");
                    Files.write(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xml).getBytes(StandardCharsets.UTF_8), transFile);
                    shell.setXml(transFile.getCanonicalPath());
                    shell.setReference((String) transResult.get("referenceIds"));
                    shell.setExecutable(true);
                } catch (Exception e) {
                    shell.setXml(null);
                    shell.setExecutable(false);
                }
            } else if (Constant.JOB.equals(shell.getCategory())) {
                try {
                    Map<String, Object> jobResult = etlGeneratorService.getJobMeta(shell);
                    JobMeta jobMeta = (JobMeta) jobResult.get("jobMeta");
                    String reference = (String) jobResult.get("referenceIds");
                    if (StringUtils.hasLength(reference)) {
                        String[] references = reference.split(",");
                        for (String referenceId : references) {
                            Shell referenceShell = one(Long.parseLong(referenceId), shell.getTenant().getId());
                            if (Constant.STREAMING.equals(referenceShell.getStreaming())) {
                                shell.setStreaming(Constant.STREAMING);
                            }
                        }
                    }
                    String xml = jobMeta.getXML();
                    String path = devDir + tenant.getId() + File.separator + shell.getProject().getId() + File.separator + shell.getShell().getId();
                    File folder = new File(path);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    File jobFile = new File(path+ File.separator + shell.getName() + ".kjb");
                    Files.write(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xml).getBytes(StandardCharsets.UTF_8), jobFile);
                    shell.setXml(jobFile.getCanonicalPath());
                    shell.setReference((String) jobResult.get("referenceIds"));
                    shell.setExecutable(true);
                } catch (Exception e) {
                    shell.setXml(null);
                    shell.setExecutable(false);
                }
            }
        }
        return shellRepository.save(shell);
    }
}
