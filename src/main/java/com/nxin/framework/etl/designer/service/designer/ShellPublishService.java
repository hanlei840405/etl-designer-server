package com.nxin.framework.etl.designer.service.designer;

import com.google.common.io.Files;
import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.entity.designer.RunningProcess;
import com.nxin.framework.etl.designer.entity.designer.Shell;
import com.nxin.framework.etl.designer.entity.designer.ShellPublish;
import com.nxin.framework.etl.designer.entity.log.ShellPublishLog;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.exception.RecordsNotMatchException;
import com.nxin.framework.etl.designer.repository.designer.ShellPublishRepository;
import com.nxin.framework.etl.designer.service.log.ShellPublishLogService;
import com.nxin.framework.etl.designer.service.task.EtlTaskComp;
import com.nxin.framework.etl.designer.vo.PageVo;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.pentaho.di.core.DBCache;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.LoggingRegistry;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobConfiguration;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.www.CarteObjectEntry;
import org.pentaho.di.www.CarteSingleton;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.nxin.framework.etl.designer.enums.Constant.JOB;

@Slf4j
@Service
public class ShellPublishService {
    @Value("${dev.dir}")
    private String devDir;
    @Value("${publish.dir}")
    private String publishDir;
    @Value("${production.dir}")
    private String productionDir;
    @Autowired
    private ShellPublishRepository shellPublishRepository;
    @Autowired
    private EtlGeneratorService etlGeneratorService;
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private RunningProcessService runningProcessService;
    @Qualifier("taskExecutor")
    @Autowired
    private Executor taskExecutor;
    @Autowired
    private ShellPublishLogService shellPublishLogService;

    public ShellPublish one(Long id) {
        return shellPublishRepository.getOne(id);
    }

    public ShellPublish one(Long id, Long tenantId) {
        return shellPublishRepository.getFirstByIdAndTenantId(id, tenantId);
    }

    public ShellPublish online(Long shellId, Long tenantId) {
        return shellPublishRepository.getFirstByShellIdAndProdAndTenantId(shellId, Constant.ACTIVE, tenantId, Sort.by(Sort.Order.desc("createTime")));
    }

    public PageVo<ShellPublish> online(String streaming, long[] projectIds, Long tenantId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Order.desc("createTime")));
        Page<ShellPublish> pageRecord = shellPublishRepository.findAllByProdAndShellCategoryAndStreamingAndShellProjectIdInAndTenantId(Constant.ACTIVE, Constant.JOB, streaming, projectIds, tenantId, pageable);
        pageRecord.getContent().forEach(item -> {
            try {
                if (StringUtils.hasLength(item.getTaskId())) {
                    TriggerKey triggerKey = TriggerKey.triggerKey(item.getTaskId());
                    CronTriggerImpl cronTrigger = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
                    if (cronTrigger != null) {
                        item.setCron(cronTrigger.getCronExpression());
                        item.setStatus(scheduler.getTriggerState(triggerKey).name());
                    }
                }
            } catch (SchedulerException e) {
                log.error(e.toString());
            }
        });
        return new PageVo(pageRecord.getTotalElements(), pageRecord.getContent());
    }

    public PageVo<ShellPublish> findHistories(Long shellId, Long tenantId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Order.desc("createTime")));
        Page<ShellPublish> pageRecord = shellPublishRepository.findAllByShellIdAndTenantId(shellId, tenantId, pageable);
        return new PageVo(pageRecord.getTotalElements(), pageRecord.getContent());
    }

    public List<ShellPublish> references(ShellPublish shellPublish, Long tenantId) {
        if (StringUtils.hasLength(shellPublish.getReference())) {
            long[] ids = Arrays.asList(shellPublish.getReference().split(",")).stream().mapToLong(Long::parseLong).toArray();
            return shellPublishRepository.findAllByIdInAndTenantId(ids, tenantId);
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 创建历史版本
     *
     * @param shell
     */
    @Transactional
    public void save(Shell shell, String description, Tenant tenant) throws RecordsNotMatchException, IOException {
        if (shell.isExecutable()) {
            String isStreaming = Constant.BATCH;
            Map<String, Object> result;
            if (Constant.JOB.equals(shell.getCategory())) {
                result = etlGeneratorService.getJobMeta(shell);
            } else {
                result = etlGeneratorService.getTransMeta(shell, tenant.getId(), true);
                TransMeta transMeta = (TransMeta) result.get("transMeta");
                StepMeta[] stepMetas = transMeta.getStepsArray();
                for (StepMeta stepMeta : stepMetas) {
                    if (Constant.STREAMING_STEP.contains(stepMeta.getTypeId())) {
                        isStreaming = Constant.STREAMING;
                    }
                }
            }
            String id = UUID.randomUUID().toString();
            ShellPublish shellPublish = new ShellPublish();
            shellPublish.setBusinessId(id);
            shellPublish.setTenant(shell.getTenant());
            shellPublish.setShell(shell);
            shellPublish.setDescription(description);
            shellPublish.setContent(shell.getContent());
            shellPublish.setStreaming(isStreaming);
            // 将脚本文件目录指定到publish目录下
            shellPublish.setCreator(shell.getCreator());
            shellPublish.setCreateTime(new Date());
            String reference = (String) result.get("referenceIds");
            List<ShellPublish> referencedList = new ArrayList<>(0);
            if (StringUtils.hasLength(reference)) {
                String[] references = reference.split(",");
                List<String> sprIds = new ArrayList<>(0);
                List<ShellPublish> existedList = new ArrayList<>(0);
                for (String referenceId : references) {
                    ShellPublish sp = shellPublishRepository.findFirstByShellIdAndTenantId(Long.parseLong(referenceId), tenant.getId(), Sort.by(Sort.Order.desc("id")));
                    if (sp != null) {
                        existedList.add(sp);
                        sprIds.add(sp.getId().toString());
                    }
                }
                // 如果关联脚本有未发布的，将抛出异常
                if (existedList.size() != references.length) {
                    throw new RecordsNotMatchException();
                }
                referencedList.addAll(existedList);
                shellPublish.setReference(String.join(",", sprIds));
            }
            // 将脚本文件复制到publish目录下
            File folder = new File(publishDir + tenant.getId() + File.separator + shell.getProject().getId() + File.separator + shell.getShell().getId() + File.separator + shellPublish.getBusinessId());
            if (!folder.exists()) {
                folder.mkdirs();
            }
            String fileName = shell.getName().concat(shell.getCategory().equals(Constant.TRANSFORM) ? ".ktr" : ".kjb");
            String direct = publishDir + tenant.getId() + File.separator + shell.getProject().getId() + File.separator + shell.getShell().getId() + File.separator + shellPublish.getBusinessId() + File.separator;
            File target = new File(direct + fileName);
            Files.copy(new File(shell.getXml()), target);
            modifyFileName(Arrays.asList(target.getCanonicalPath()), direct);
            shellPublish.setXml(target.getCanonicalPath());
            referencedList.add(shellPublish);
            shellPublishRepository.saveAll(referencedList);
        }
    }

    /**
     * 上线新版并下线旧版
     *
     * @param shellPublish
     */
    @Transactional
    public void deploySchedule(ShellPublish shellPublish, String cron, int misfire) throws IOException, SchedulerException {
        ShellPublish published = shellPublishRepository.findFirstByProdAndIdNotAndShellIdAndTenantId(Constant.ACTIVE, shellPublish.getId(), shellPublish.getShell().getId(), shellPublish.getTenant().getId());
        String reference;
        List<ShellPublish> referencedList = new ArrayList<>(0);
        if (published != null) {
            published.setProd(Constant.INACTIVE);
            // 将关联脚本也一并下线
            reference = published.getReference();
            if (StringUtils.hasLength(reference)) {
                long[] ids = Arrays.stream(reference.split(",")).mapToLong(Long::parseLong).toArray();
                referencedList = shellPublishRepository.findAllByIdInAndTenantId(ids, shellPublish.getTenant().getId()).stream().peek(spr -> spr.setProd(Constant.INACTIVE)).collect(Collectors.toList());
            }
            if (StringUtils.hasLength(published.getTaskId())) {
                if (StringUtils.hasLength(published.getTaskId())) {
                    if (scheduler.checkExists(TriggerKey.triggerKey(published.getTaskId()))) {
                        TriggerKey triggerKey = TriggerKey.triggerKey(published.getTaskId());
                        scheduler.pauseTrigger(triggerKey);
                        scheduler.unscheduleJob(triggerKey);
                        if (scheduler.checkExists(JobKey.jobKey(published.getTaskId()))) {
                            scheduler.deleteJob(JobKey.jobKey(published.getTaskId()));
                        }
                    }
                }
            }
            referencedList.add(published);
        }
        String taskId;
        if (StringUtils.hasLength(shellPublish.getTaskId())) {
            taskId = shellPublish.getTaskId();
            // 关闭当前任务
            if (scheduler.checkExists(TriggerKey.triggerKey(shellPublish.getTaskId()))) {
                TriggerKey triggerKey = TriggerKey.triggerKey(shellPublish.getTaskId());
                scheduler.pauseTrigger(triggerKey);
                scheduler.unscheduleJob(triggerKey);
                if (scheduler.checkExists(JobKey.jobKey(shellPublish.getTaskId()))) {
                    scheduler.deleteJob(JobKey.jobKey(shellPublish.getTaskId()));
                }
            }
        } else {
            taskId = UUID.randomUUID().toString();
            shellPublish.setTaskId(taskId);
        }
        // 创建生产环境目录
        File folder = new File(productionDir + shellPublish.getTenant().getId() + File.separator + shellPublish.getShell().getProject().getId() + File.separator + shellPublish.getShell().getId());
        if (!folder.exists()) {
            folder.mkdirs();
        }
        List<String> files = new ArrayList<>(0);
        // 将新关联的脚本启用上线
        reference = shellPublish.getReference();
        if (StringUtils.hasLength(reference)) {
            long[] ids = Arrays.stream(reference.split(",")).mapToLong(Long::parseLong).toArray();
            List<ShellPublish> existedList = shellPublishRepository.findAllByIdInAndTenantId(ids, shellPublish.getTenant().getId()).stream().peek(spr -> {
                spr.setProd(Constant.ACTIVE);
                try {
                    // 复制到生产环境
                    String fileName = spr.getShell().getName().concat(spr.getShell().getCategory().equals(Constant.TRANSFORM) ? ".ktr" : ".kjb");
                    String direct = productionDir + shellPublish.getShell().getProject().getTenant().getId() + File.separator + shellPublish.getShell().getProject().getId() + File.separator + shellPublish.getShell().getId() + File.separator;
                    File target = new File(direct + fileName);
                    Files.copy(new File(spr.getXml()), target);
                    files.add(target.getCanonicalPath());
                    spr.setProdPath(direct + fileName);
                } catch (IOException e) {
                    log.error(e.toString());
                }
            }).collect(Collectors.toList());
            referencedList.addAll(existedList);
        }
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        if (misfire == -1) {
            cronScheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
        } else if (misfire == 1) {
            cronScheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
        } else {
            cronScheduleBuilder.withMisfireHandlingInstructionDoNothing();
        }
        CronTrigger kettleTrigger = TriggerBuilder.newTrigger().withIdentity(taskId).withSchedule(cronScheduleBuilder).build();
        JobDetail jobDetail = JobBuilder.newJob(EtlTaskComp.class).withIdentity(taskId).build();
        String fileName = shellPublish.getShell().getName().concat(shellPublish.getShell().getCategory().equals(Constant.TRANSFORM) ? ".ktr" : ".kjb");
        String direct = productionDir + shellPublish.getShell().getProject().getTenant().getId() + File.separator + shellPublish.getShell().getProject().getId() + File.separator + shellPublish.getShell().getId() + File.separator;
        File target = new File(direct + fileName);
        Files.copy(new File(shellPublish.getXml()), target);
        files.add(target.getCanonicalPath());
        modifyFileName(files, direct);
        jobDetail.getJobDataMap().put("path", target.getCanonicalPath());
        jobDetail.getJobDataMap().put("shellId", shellPublish.getShell().getId());
        jobDetail.getJobDataMap().put("shellPublishId", shellPublish.getId());
        scheduler.scheduleJob(jobDetail, kettleTrigger);
        shellPublish.setProdPath(direct + fileName);
        referencedList.add(shellPublish);
        shellPublishRepository.saveAll(referencedList);
    }

    /**
     * 上线新版并下线旧版
     *
     * @param shellPublish
     */
    @Transactional
    public void deployStreaming(ShellPublish shellPublish) throws Exception {
        ShellPublish published = shellPublishRepository.findFirstByProdAndIdNotAndShellIdAndTenantId(Constant.ACTIVE, shellPublish.getId(), shellPublish.getShell().getId(), shellPublish.getTenant().getId());
        String reference;
        List<ShellPublish> referencedList = new ArrayList<>(0);
        // 找到已发行的版本，停止运行
        if (published != null) {
            published.setProd(Constant.INACTIVE);
            // 将关联脚本也一并下线
            reference = published.getReference();
            if (StringUtils.hasLength(reference)) {
                long[] ids = Arrays.stream(reference.split(",")).mapToLong(Long::parseLong).toArray();
                referencedList = shellPublishRepository.findAllByIdInAndTenantId(ids, shellPublish.getTenant().getId()).stream().peek(spr -> spr.setProd(Constant.INACTIVE)).collect(Collectors.toList());
            }
            // 如果之前任务为schedule任务，则将之前发布的任务停止
            if (StringUtils.hasLength(published.getTaskId())) {
                if (scheduler.checkExists(TriggerKey.triggerKey(published.getTaskId()))) {
                    TriggerKey triggerKey = TriggerKey.triggerKey(published.getTaskId());
                    scheduler.pauseTrigger(triggerKey);
                    scheduler.unscheduleJob(triggerKey);
                    if (scheduler.checkExists(JobKey.jobKey(published.getTaskId()))) {
                        scheduler.deleteJob(JobKey.jobKey(published.getTaskId()));
                    }
                }
            }
            referencedList.add(published);
        }
        String taskId;
        if (StringUtils.hasLength(shellPublish.getTaskId())) {
            taskId = shellPublish.getTaskId();
            // 关闭当前任务
            if (scheduler.checkExists(TriggerKey.triggerKey(shellPublish.getTaskId()))) {
                TriggerKey triggerKey = TriggerKey.triggerKey(shellPublish.getTaskId());
                scheduler.pauseTrigger(triggerKey);
                scheduler.unscheduleJob(triggerKey);
                if (scheduler.checkExists(JobKey.jobKey(shellPublish.getTaskId()))) {
                    scheduler.deleteJob(JobKey.jobKey(shellPublish.getTaskId()));
                }
            }
        } else {
            taskId = UUID.randomUUID().toString();
            shellPublish.setTaskId(taskId);
        }
        // 创建生产环境目录
        File folder = new File(productionDir + shellPublish.getShell().getProject().getTenant().getId() + File.separator + shellPublish.getShell().getProject().getId() + File.separator + shellPublish.getShell().getId());
        if (!folder.exists()) {
            folder.mkdirs();
        }
        List<String> files = new ArrayList<>(0);
        // 将新关联的脚本启用上线
        reference = shellPublish.getReference();
        if (StringUtils.hasLength(reference)) {
            long[] ids = Arrays.stream(reference.split(",")).mapToLong(Long::parseLong).toArray();
            List<ShellPublish> existedList = shellPublishRepository.findAllByIdInAndTenantId(ids, shellPublish.getTenant().getId()).stream().peek(spr -> {
                spr.setProd(Constant.ACTIVE);
                try {
                    // 复制到生产环境
                    String fileName = spr.getShell().getName().concat(spr.getShell().getCategory().equals(Constant.TRANSFORM) ? ".ktr" : ".kjb");
                    String direct = productionDir + shellPublish.getShell().getProject().getTenant().getId() + File.separator + shellPublish.getShell().getProject().getId() + File.separator + shellPublish.getShell().getId() + File.separator;
                    File target = new File(direct + fileName);
                    Files.copy(new File(spr.getXml()), target);
                    files.add(target.getCanonicalPath());
                    spr.setProdPath(direct + fileName);
                } catch (IOException e) {
                    log.error(e.toString());
                }
            }).collect(Collectors.toList());
            referencedList.addAll(existedList);
        }
        String fileName = shellPublish.getShell().getName().concat(shellPublish.getShell().getCategory().equals(Constant.TRANSFORM) ? ".ktr" : ".kjb");
        String direct = productionDir + shellPublish.getShell().getProject().getTenant().getId() + File.separator + shellPublish.getShell().getProject().getId() + File.separator + shellPublish.getShell().getId() + File.separator;
        File target = new File(direct + fileName);
        Files.copy(new File(shellPublish.getXml()), target);
        files.add(target.getCanonicalPath());
        modifyFileName(files, direct);
        shellPublish.setProdPath(direct + fileName);
        referencedList.add(shellPublish);
        shellPublishRepository.saveAll(referencedList);
        DBCache.getInstance().clear(null);
        SimpleLoggingObject spoonLoggingObject = new SimpleLoggingObject("SPOON", LoggingObjectType.SPOON, null);
        spoonLoggingObject.setContainerObjectId(taskId);
        JobExecutionConfiguration jobExecutionConfiguration = new JobExecutionConfiguration();
        jobExecutionConfiguration.setLogLevel(LogLevel.BASIC);
        JobMeta jobMeta = new JobMeta(shellPublish.getProdPath(), null);
        jobMeta.getXML();
        JobConfiguration jobConfiguration = new JobConfiguration(jobMeta, jobExecutionConfiguration);
        spoonLoggingObject.setLogLevel(jobExecutionConfiguration.getLogLevel());
        Job job = new Job(null, jobMeta, spoonLoggingObject);
        job.injectVariables(jobConfiguration.getJobExecutionConfiguration().getVariables());
        job.setGatheringMetrics(true);
        job.getJobMeta().getXML();
        job.start();
        shellPublishLogService.delete(shellPublish.getId());
        taskExecutor.execute(() -> {
            CarteSingleton.getInstance().getJobMap().addJob(job.getName(), taskId, job, jobConfiguration);
            while (!job.isStopped() && !job.isFinished()) {
                log.info("【{}】正在运行", job.getName());
                List<String> logChannelIds = LoggingRegistry.getInstance().getLogChannelChildren(job.getLogChannelId());
                shellPublishLogService.save(logChannelIds.stream().map(logChannelId -> new ShellPublishLog(logChannelId, shellPublish)).collect(Collectors.toList()));
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    log.error(e.toString());
                }
            }
            log.info("【{}】中断运行", job.getName());
            List<String> logChannelIds = LoggingRegistry.getInstance().getLogChannelChildren(job.getLogChannelId());
            shellPublishLogService.save(logChannelIds.stream().map(logChannelId -> new ShellPublishLog(logChannelId, shellPublish)).collect(Collectors.toList()));
        });
        // 将流处理任务纳入进程管理，可随时中断
        RunningProcess runningProcess = new RunningProcess();
        runningProcess.setProd("1");
        runningProcess.setOwner("task");
        runningProcess.setShellPublish(shellPublish);
        runningProcess.setInstanceId(taskId);
        runningProcess.setInstanceName(job.getName());
        runningProcess.setCategory(JOB);
        runningProcessService.save(runningProcess, shellPublish.getTenant());
    }

    @Transactional
    public ShellPublish stop(ShellPublish shellPublish) throws SchedulerException {
        if (Constant.BATCH.equals(shellPublish.getStreaming())) {
            if (scheduler.checkExists(TriggerKey.triggerKey(shellPublish.getTaskId()))) {
                scheduler.pauseTrigger(TriggerKey.triggerKey(shellPublish.getTaskId()));
                scheduler.unscheduleJob(TriggerKey.triggerKey(shellPublish.getTaskId()));
            }
            if (scheduler.checkExists(JobKey.jobKey(shellPublish.getTaskId()))) {
                scheduler.deleteJob(JobKey.jobKey(shellPublish.getTaskId()));
            }
        } else {
            List<RunningProcess> runningProcessList = runningProcessService.instanceId(shellPublish.getTaskId(), shellPublish.getTenant().getId());
            if (!runningProcessList.isEmpty()) {
                CarteObjectEntry carteObjectEntry = new CarteObjectEntry(runningProcessList.get(0).getInstanceName(), runningProcessList.get(0).getInstanceId());
                Job job = CarteSingleton.getInstance().getJobMap().getJob(carteObjectEntry);
                job.stopAll();
                CarteSingleton.getInstance().getJobMap().removeJob(carteObjectEntry);
                runningProcessService.delete(runningProcessList);
            }
        }
        shellPublish.setProd(Constant.INACTIVE);
        return shellPublishRepository.save(shellPublish);
    }

    private void modifyFileName(List<String> files, String target) {
        files.forEach(file -> {
            try {
                SAXReader reader = new SAXReader();
                Document document = reader.read(new File(file));
                Element rootElement = document.getRootElement();
                if (file.toLowerCase(Locale.ROOT).endsWith(".kjb")) {
                    List<Element> entries = rootElement.element("entries").elements();
                    for (Element entry : entries) {
                        Element filenameElement = entry.element("filename");
                        if (filenameElement != null) {
                            Element nameElement = entry.element("name");
                            if (nameElement != null && StringUtils.hasLength(nameElement.getText())) {
                                String value = filenameElement.getTextTrim();
                                if (StringUtils.hasLength(value)) {
                                    String[] path = value.split(File.separator);
                                    filenameElement.setText(target + path[path.length - 1]);
                                }
                            }
                        }
                    }
                } else {
                    List<Element> steps = rootElement.element("step").elements();
                    for (Element step : steps) {
                        if ("transformationPath".equals(step.getName())) {
                            if (StringUtils.hasLength(step.getText())) {
                                String value = step.getTextTrim();
                                if (StringUtils.hasLength(value)) {
                                    String[] path = value.split(File.separator);
                                    step.setText(target + path[path.length - 1]);
                                }
                            }
                        }
                    }
                }
                //格式化为缩进格式
                OutputFormat format = OutputFormat.createPrettyPrint();
                //设置编码格式
                format.setEncoding("UTF-8");
                XMLWriter writer = new XMLWriter(new FileWriter(file), format);
                //写入数据
                writer.write(document);
                writer.close();
            } catch (Exception e) {
                log.error(e.toString());
            }
        });
    }
}
