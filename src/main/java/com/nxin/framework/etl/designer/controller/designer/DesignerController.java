package com.nxin.framework.etl.designer.controller.designer;

import com.nxin.framework.etl.designer.dto.CrudDto;
import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.entity.designer.RunningProcess;
import com.nxin.framework.etl.designer.entity.designer.Shell;
import com.nxin.framework.etl.designer.service.basic.UserService;
import com.nxin.framework.etl.designer.service.designer.EtlGeneratorService;
import com.nxin.framework.etl.designer.service.designer.RunningProcessService;
import com.nxin.framework.etl.designer.service.designer.ShellService;
import com.nxin.framework.etl.designer.service.log.LogService;
import com.nxin.framework.etl.designer.vo.log.StepLogVo;
import com.nxin.framework.etl.designer.vo.log.TransformLogVo;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.LoggingRegistry;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobConfiguration;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransConfiguration;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.www.CarteObjectEntry;
import org.pentaho.di.www.CarteSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static com.nxin.framework.etl.designer.enums.Constant.*;

@PreAuthorize("hasAuthority('ROOT') or hasAuthority('DESIGNER')")
@Slf4j
@RestController
@RequestMapping("/designer")
public class DesignerController {
    @Autowired
    private LogService logService;
    @Autowired
    private EtlGeneratorService etlGeneratorService;
    @Autowired
    private ShellService shellService;
    @Autowired
    private UserService userService;
    @Autowired
    private RunningProcessService runningProcessService;
    @Qualifier("taskExecutor")
    @Autowired
    private Executor taskExecutor;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private static final String CHANNEL_TYPE_TRANS = "TRANS";

    private static final String CHANNEL_TYPE_STEP = "STEP";

    private static final String CHANNEL_TYPE_JOB = "JOB";

    @PostMapping("/execute/{id}")
    public ResponseEntity<Map<String, Object>> execute(@PathVariable("id") String id, @RequestBody Shell shell, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Shell existed = shellService.one(shell.getId(), loginUser.getTenant().getId());
        if (existed != null && existed.getProject().getUsers().contains(loginUser)) {
            RunningProcess runningProcess = new RunningProcess();
            runningProcess.setProd("0");
            runningProcess.setOwner("designer");
            runningProcess.setShell(existed);
            runningProcess.setInstanceId(id);
            shell.setProject(existed.getProject());
            SimpleLoggingObject spoonLoggingObject = new SimpleLoggingObject("SPOON", LoggingObjectType.SPOON, null);
            spoonLoggingObject.setContainerObjectId(id);
            LoggingRegistry loggingRegistry = LoggingRegistry.getInstance();
            if (TRANSFORM.equals(existed.getCategory())) {
                TransExecutionConfiguration transExecutionConfiguration = new TransExecutionConfiguration();
                transExecutionConfiguration.setLogLevel(LogLevel.BASIC);
                Map<String, Object> transMap = etlGeneratorService.getTransMeta(shell, loginUser.getTenant().getId(), false);
                TransMeta transMeta = (TransMeta) transMap.get("transMeta");
                TransConfiguration transConfiguration = new TransConfiguration(transMeta, transExecutionConfiguration);
                spoonLoggingObject.setLogLevel(transExecutionConfiguration.getLogLevel());
                Trans trans = new Trans(transMeta, spoonLoggingObject);
                try {
                    trans.injectVariables(transConfiguration.getTransExecutionConfiguration().getVariables());
                    trans.setGatheringMetrics(true);
                    transMeta.getXML();
                    // 空参调用
                    trans.execute(new String[]{});
                    taskExecutor.execute(() -> {
                        CarteSingleton.getInstance().getTransformationMap().addTransformation(trans.getName(), id, trans, transConfiguration);
                        Map<String, Object> response;
                        while (trans.isRunning()) {
                            List<String> logChannelIds = loggingRegistry.getLogChannelChildren(trans.getLogChannelId());
                            response = this.fetchLogs(logChannelIds);
                            response.put("running", true);
                            simpMessagingTemplate.convertAndSend(id, response);
                            try {
                                TimeUnit.SECONDS.sleep(5);
                            } catch (InterruptedException e) {
                                log.error(e.toString());
                            }
                        }
                        CarteSingleton.getInstance().getTransformationMap().removeTransformation(new CarteObjectEntry(trans.getName(), id));
                        runningProcessService.delete(runningProcess);
                        List<String> logChannelIds = loggingRegistry.getLogChannelChildren(trans.getLogChannelId());
                        response = this.fetchLogs(logChannelIds);
                        response.put("running", false);
                        simpMessagingTemplate.convertAndSend(id, response);
                    });
                    runningProcess.setInstanceName(trans.getName());
                    runningProcess.setCategory(TRANSFORM);
                    runningProcessService.save(runningProcess, loginUser.getTenant());
                    return ResponseEntity.ok(Collections.EMPTY_MAP);
                } catch (Exception e) {
                    e.printStackTrace();
                    List<String> logChannelIds = loggingRegistry.getLogChannelChildren(trans.getLogChannelId());
                    Map<String, Object> error = this.fetchLogs(logChannelIds);
                    error.put("error", e.toString() + "\r\n" + e.getStackTrace()[0].toString());
                    simpMessagingTemplate.convertAndSend(id, error);
                    return ResponseEntity.status(EXCEPTION_ETL_GRAMMAR).body(error);
                }
            } else if (JOB.equals(existed.getCategory())) {
                JobExecutionConfiguration jobExecutionConfiguration = new JobExecutionConfiguration();
                jobExecutionConfiguration.setLogLevel(LogLevel.BASIC);
                Map<String, Object> jobResult = etlGeneratorService.getJobMeta(shell);
                JobMeta jobMeta = (JobMeta) jobResult.get("jobMeta");
                JobConfiguration jobConfiguration = new JobConfiguration(jobMeta, jobExecutionConfiguration);
                spoonLoggingObject.setContainerObjectId(id);
                spoonLoggingObject.setLogLevel(jobExecutionConfiguration.getLogLevel());
                jobMeta.getXML();
                Job job = new Job(null, jobMeta, spoonLoggingObject);
                try {
                    job.injectVariables(jobConfiguration.getJobExecutionConfiguration().getVariables());
                    job.setGatheringMetrics(true);
                    job.start();
                    taskExecutor.execute(() -> {
                        CarteSingleton.getInstance().getJobMap().addJob(job.getName(), id, job, jobConfiguration);
                        Map<String, Object> response;
                        while (!job.isStopped() && !job.isFinished()) {
                            log.info("【{}】正在运行", job.getName());
                            List<String> logChannelIds = loggingRegistry.getLogChannelChildren(job.getLogChannelId());
                            response = this.fetchLogs(logChannelIds);
                            response.put("running", true);
                            simpMessagingTemplate.convertAndSend(id, response);
                            try {
                                TimeUnit.SECONDS.sleep(5);
                            } catch (InterruptedException e) {
                                log.error(e.toString());
                            }
                        }
//                        job.waitUntilFinished();
                        CarteSingleton.getInstance().getJobMap().removeJob(new CarteObjectEntry(job.getName(), id));
                        runningProcessService.delete(runningProcess);
                        List<String> logChannelIds = loggingRegistry.getLogChannelChildren(job.getLogChannelId());
                        response = this.fetchLogs(logChannelIds);
                        response.put("running", false);
                        simpMessagingTemplate.convertAndSend(id, response);
                    });
                    runningProcess.setInstanceName(job.getName());
                    runningProcess.setCategory(JOB);
                    runningProcessService.save(runningProcess, loginUser.getTenant());
                    return ResponseEntity.ok(Collections.EMPTY_MAP);
                } catch (Exception e) {
                    List<String> logChannelIds = loggingRegistry.getLogChannelChildren(job.getLogChannelId());
                    Map<String, Object> error = this.fetchLogs(logChannelIds);
                    error.put("error", e.toString() + "\r\n" + e.getStackTrace()[0].toString());
                    simpMessagingTemplate.convertAndSend(id, error);
                    return ResponseEntity.status(EXCEPTION_ETL_GRAMMAR).body(error);
                }
            } else {
                return ResponseEntity.status(EXCEPTION_NOT_FOUNT).build();
            }
        }
        return ResponseEntity.status(EXCEPTION_UNAUTHORIZED).build();
    }

    @PostMapping("/pause")
    public ResponseEntity<String> pause(@RequestBody CrudDto crudDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Shell existed = shellService.one(crudDto.getId(), loginUser.getTenant().getId());
        if (existed != null && existed.getProject().getUsers().contains(loginUser)) {
            CarteObjectEntry carteObjectEntry = new CarteObjectEntry(existed.getName(), crudDto.getPayload());
            Trans trans = CarteSingleton.getInstance().getTransformationMap().getTransformation(carteObjectEntry);
            if (trans != null) {
                trans.pauseRunning();
                return ResponseEntity.ok(trans.getStatus());
            }
            return ResponseEntity.status(EXCEPTION_NOT_FOUNT).build();
        }
        return ResponseEntity.status(EXCEPTION_UNAUTHORIZED).build();
    }

    @PostMapping("/stop")
    public ResponseEntity<Map<String, Object>> stop(@RequestBody CrudDto crudDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Shell existed = shellService.one(crudDto.getId(), loginUser.getTenant().getId());
        if (existed != null && existed.getProject().getUsers().contains(loginUser)) {
            try {
                Map<String, Object> result = new HashMap<>(0);
                CarteObjectEntry carteObjectEntry = new CarteObjectEntry(existed.getName(), crudDto.getPayload());
                if (JOB.equals(existed.getCategory())) {
                    Job job = CarteSingleton.getInstance().getJobMap().getJob(carteObjectEntry);
                    if (job != null) {
                        job.stopAll();
                        CarteSingleton.getInstance().getJobMap().removeJob(new CarteObjectEntry(job.getName(), crudDto.getPayload()));
                        runningProcessService.delete(runningProcessService.instanceId(crudDto.getPayload(), loginUser.getTenant().getId()));
                        return ResponseEntity.ok(result);
                    }
                } else {
                    Trans trans = CarteSingleton.getInstance().getTransformationMap().getTransformation(carteObjectEntry);
                    if (trans != null) {
                        trans.stopAll();
                        CarteSingleton.getInstance().getTransformationMap().removeTransformation(new CarteObjectEntry(trans.getName(), crudDto.getPayload()));
                        runningProcessService.delete(runningProcessService.instanceId(crudDto.getPayload(), loginUser.getTenant().getId()));
                        return ResponseEntity.ok(result);
                    }
                }
            } catch (NullPointerException e) {
                return ResponseEntity.status(EXCEPTION_NOT_FOUNT).build();
            }
            return ResponseEntity.status(EXCEPTION_NOT_FOUNT).build();
        }
        return ResponseEntity.status(EXCEPTION_UNAUTHORIZED).build();
    }

    private Map<String, Object> fetchLogs(List<String> logChannelIds) {
        StringBuilder builder = new StringBuilder();
        List<StepLogVo> stepLogs = new ArrayList<>(0);
        List<TransformLogVo> transformLogs = logService.transformLog(logChannelIds);
        transformLogs.forEach(log -> builder.append(log.getLogField()));
        stepLogs.addAll(logService.stepLog(logChannelIds));
        Map<String, Object> result = new HashMap<>(0);
        result.put("log", builder.toString());
        result.put("steps", stepLogs);
        return result;
    }
}
