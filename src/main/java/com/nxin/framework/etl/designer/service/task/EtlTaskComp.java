package com.nxin.framework.etl.designer.service.task;

import com.nxin.framework.etl.designer.entity.designer.RunningProcess;
import com.nxin.framework.etl.designer.entity.designer.ShellPublish;
import com.nxin.framework.etl.designer.entity.log.ShellPublishLog;
import com.nxin.framework.etl.designer.service.designer.RunningProcessService;
import com.nxin.framework.etl.designer.service.designer.ShellPublishService;
import com.nxin.framework.etl.designer.service.log.ShellPublishLogService;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobConfiguration;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.www.CarteObjectEntry;
import org.pentaho.di.www.CarteSingleton;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.UUID;
import java.util.concurrent.Executor;

import static com.nxin.framework.etl.designer.enums.Constant.JOB;

@Slf4j
@Component
public class EtlTaskComp extends QuartzJobBean {
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Qualifier("taskExecutor")
    @Autowired
    private Executor taskExecutor;
    @Autowired
    private ShellPublishService shellPublishService;
    @Autowired
    private RunningProcessService runningProcessService;
    @Autowired
    private ShellPublishLogService shellPublishLogService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityManagerHolder emHolder = new EntityManagerHolder(entityManager);
        TransactionSynchronizationManager.bindResource(entityManagerFactory, emHolder);
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String path = jobDataMap.getString("path");
        Long shellPublishId = jobDataMap.getLong("shellPublishId");
        SimpleLoggingObject spoonLoggingObject = new SimpleLoggingObject("SPOON", LoggingObjectType.SPOON, null);
        String id = UUID.randomUUID().toString();
        spoonLoggingObject.setContainerObjectId(id);
        JobExecutionConfiguration jobExecutionConfiguration = new JobExecutionConfiguration();
        jobExecutionConfiguration.setLogLevel(LogLevel.BASIC);
        try {
            JobMeta jobMeta = new JobMeta(path, null);
            jobMeta.getXML();
            JobConfiguration jobConfiguration = new JobConfiguration(jobMeta, jobExecutionConfiguration);
            spoonLoggingObject.setLogLevel(jobExecutionConfiguration.getLogLevel());
            Job job = new Job(null, jobMeta, spoonLoggingObject);
            job.injectVariables(jobConfiguration.getJobExecutionConfiguration().getVariables());
            job.setGatheringMetrics(true);
            job.start();
            CarteSingleton.getInstance().getJobMap().addJob(job.getName(), id, job, jobConfiguration);
            ShellPublish shellPublish = shellPublishService.one(shellPublishId);
            RunningProcess runningProcess = new RunningProcess();
            runningProcess.setProd("1");
            runningProcess.setOwner("task");
            runningProcess.setShellPublish(shellPublish);
            runningProcess.setShell(shellPublish.getShell());
            runningProcess.setInstanceId(id);
            runningProcess.setInstanceName(job.getName());
            runningProcess.setCategory(JOB);
            runningProcessService.save(runningProcess, shellPublish.getTenant());
            taskExecutor.execute(() -> {
                job.waitUntilFinished();
                CarteSingleton.getInstance().getJobMap().removeJob(new CarteObjectEntry(job.getName(), id));
                shellPublishLogService.save(new ShellPublishLog(job.getLogChannelId(), shellPublish));
                runningProcessService.delete(runningProcess);
            });
            if (job.getErrors() > 0) {
                log.error("执行" + path + "发生异常, {}");
            }
        } catch (KettleXMLException e) {
            e.printStackTrace();
        } finally {
            TransactionSynchronizationManager.unbindResource(entityManagerFactory);
            EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
        }
    }
}
