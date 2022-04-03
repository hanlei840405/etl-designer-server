package com.nxin.framework.etl.designer.controller.task;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.converter.bean.designer.ShellPublishConverter;
import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.entity.designer.ShellPublish;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.service.basic.UserService;
import com.nxin.framework.etl.designer.service.designer.ShellPublishService;
import com.nxin.framework.etl.designer.service.log.LogService;
import com.nxin.framework.etl.designer.service.log.ShellPublishLogService;
import com.nxin.framework.etl.designer.vo.basic.ProjectVo;
import com.nxin.framework.etl.designer.vo.designer.ShellPublishVo;
import com.nxin.framework.etl.designer.vo.designer.ShellVo;
import com.nxin.framework.etl.designer.vo.log.JobLogVo;
import com.nxin.framework.etl.designer.vo.log.TransformLogVo;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@PreAuthorize("hasAuthority('ROOT') or hasAuthority('BATCH') or hasAuthority('STREAMING') or hasAuthority('PROCESS') or hasAuthority('LOG')")
@RestController
public class TaskController {
    @Autowired
    private UserService userService;
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private ShellPublishService shellPublishService;
    @Autowired
    private LogService logService;
    @Autowired
    private ShellPublishLogService shellPublishLogService;
    private BeanConverter<ShellPublishVo, ShellPublish> shellPublishConverter = new ShellPublishConverter();

    @PutMapping("/task/pause/{id}")
    public ResponseEntity<String> pause(@PathVariable("id") Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        ShellPublish sp = shellPublishService.one(id);
        if (sp.getShell().getProject().getUsers().contains(loginUser)) {
            try {
                scheduler.pauseJob(JobKey.jobKey(sp.getTaskId()));
                return ResponseEntity.ok(scheduler.getTriggerState(TriggerKey.triggerKey(sp.getTaskId())).name());
            } catch (SchedulerException e) {
                return ResponseEntity.status(Constant.EXCEPTION_ADD_SCHEDULE).build();
            }
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PutMapping("/task/resume/{id}")
    public ResponseEntity<String> resume(@PathVariable("id") Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        ShellPublish sp = shellPublishService.one(id);
        if (sp.getShell().getProject().getUsers().contains(loginUser)) {
            try {
                scheduler.resumeJob(JobKey.jobKey(sp.getTaskId()));
                return ResponseEntity.ok(scheduler.getTriggerState(TriggerKey.triggerKey(sp.getTaskId())).name());
            } catch (SchedulerException e) {
                return ResponseEntity.status(Constant.EXCEPTION_ADD_SCHEDULE).build();
            }
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @DeleteMapping("/task/delete/{id}")
    public ResponseEntity<ShellPublishVo> delete(@PathVariable("id") Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        ShellPublish sp = shellPublishService.one(id);
        if (sp == null || sp.getShell() == null || sp.getShell().getProject() == null || sp.getShell().getProject().getUsers().isEmpty()) {
            return ResponseEntity.status(Constant.EXCEPTION_NOT_FOUNT).build();
        }
        if (sp.getShell().getProject().getUsers().contains(loginUser)) {
            try {
                sp = shellPublishService.stop(sp);
                return ResponseEntity.ok(shellPublishConverter.convert(sp, false));
            } catch (SchedulerException e) {
                return ResponseEntity.status(Constant.EXCEPTION_REMOVE_SCHEDULE).build();
            }
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @GetMapping("/task/batch/logs/{id}")
    public ResponseEntity<List<JobLogVo>> batchLogs(@PathVariable("id") Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        ShellPublish existed = shellPublishService.one(id);
        if (existed.getShell().getProject().getUsers().contains(loginUser)) {
            List<String> logChannelIds = shellPublishLogService.allByShellPublish(existed.getId()).stream().map(shellPublishLog -> shellPublishLog.getLogChannelId()).collect(Collectors.toList());
            return ResponseEntity.ok(logService.jobLog(logChannelIds));
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @GetMapping("/task/batch/log/{id}/{channelId}")
    public ResponseEntity<JobLogVo> batchLog(@PathVariable("id") Long id, @PathVariable("channelId") String channelId, Principal principal) {
        User loginUser = userService.one(principal.getName());
        ShellPublish sp = shellPublishService.one(id);
        if (sp.getShell().getProject().getUsers().contains(loginUser)) {
            List<JobLogVo> jobLogs = logService.jobLog(Arrays.asList(channelId));
            if (!jobLogs.isEmpty()) {
                return ResponseEntity.ok(jobLogs.get(0));
            }
            return ResponseEntity.status(Constant.EXCEPTION_NOT_FOUNT).build();
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @GetMapping("/task/streaming/log/{id}")
    public ResponseEntity<String> streamingLog(@PathVariable("id") Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        ShellPublish existed = shellPublishService.one(id);
        if (existed.getShell().getProject().getUsers().contains(loginUser)) {
            List<String> logChannelIds = shellPublishLogService.allByShellPublish(existed.getId()).stream().map(shellPublishLog -> shellPublishLog.getLogChannelId()).collect(Collectors.toList());
            StringBuilder builder = new StringBuilder();
            List<TransformLogVo> transformLogs = logService.transformLog(logChannelIds);
            transformLogs.forEach(log -> builder.append(log.getLogField()));
            return ResponseEntity.ok(builder.toString());
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }
}
