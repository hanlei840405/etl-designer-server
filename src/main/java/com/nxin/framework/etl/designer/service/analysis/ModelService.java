package com.nxin.framework.etl.designer.service.analysis;

import com.nxin.framework.etl.designer.entity.analysis.Metadata;
import com.nxin.framework.etl.designer.entity.analysis.Model;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.enums.CronType;
import com.nxin.framework.etl.designer.repository.analysis.ModelRepository;
import com.nxin.framework.etl.designer.service.task.CreateTableTaskComp;
import lombok.SneakyThrows;
import org.quartz.*;
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
    @Autowired
    private Scheduler scheduler;

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
        String cron = CronType.getValue(model.getFrequency());
        // 删除之前的任务
        if (scheduler.checkExists(TriggerKey.triggerKey(model.getTenant().getId() + "#" + model.getId()))) {
            TriggerKey triggerKey = TriggerKey.triggerKey(model.getTenant().getId() + "#" + model.getId());
            scheduler.pauseTrigger(triggerKey);
            scheduler.unscheduleJob(triggerKey);
            if (scheduler.checkExists(JobKey.jobKey(model.getTenant().getId() + "#" + model.getId()))) {
                scheduler.deleteJob(JobKey.jobKey(model.getTenant().getId() + "#" + model.getId()));
            }
        }
        if (cron != null) {
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
            cronScheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
            CronTrigger kettleTrigger = TriggerBuilder.newTrigger().withIdentity(model.getTenant().getId() + "#" + model.getId()).withSchedule(cronScheduleBuilder).build();
            JobDetail jobDetail = JobBuilder.newJob(CreateTableTaskComp.class).withIdentity(model.getTenant().getId() + "#" + model.getId()).build();
            jobDetail.getJobDataMap().put("tableName", model.getCode());
            jobDetail.getJobDataMap().put("frequency", model.getFrequency());
            jobDetail.getJobDataMap().put("name", model.getDatasource().getName());
            jobDetail.getJobDataMap().put("category", model.getDatasource().getCategory());
            jobDetail.getJobDataMap().put("host", model.getDatasource().getHost());
            jobDetail.getJobDataMap().put("schemaName", model.getDatasource().getSchemaName());
            jobDetail.getJobDataMap().put("port", model.getDatasource().getPort().toString());
            jobDetail.getJobDataMap().put("username", model.getDatasource().getUsername());
            jobDetail.getJobDataMap().put("password", model.getDatasource().getPassword());
            jobDetail.getJobDataMap().put("sql", metadataService.generateSql("%s", metadataList));
            scheduler.scheduleJob(jobDetail, kettleTrigger);
        }
        return model;
    }

    @Transactional
    public Model delete(Model model) {
        modelRepository.save(model);
        return model;
    }
}
