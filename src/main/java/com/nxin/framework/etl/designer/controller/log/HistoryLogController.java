package com.nxin.framework.etl.designer.controller.log;

import com.nxin.framework.etl.designer.dto.CrudDto;
import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.entity.designer.ShellPublish;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.service.basic.UserService;
import com.nxin.framework.etl.designer.service.designer.ShellPublishService;
import com.nxin.framework.etl.designer.service.log.LogService;
import com.nxin.framework.etl.designer.service.log.ShellPublishLogService;
import com.nxin.framework.etl.designer.vo.log.ChannelLogVo;
import com.nxin.framework.etl.designer.vo.log.JobEntryLogVo;
import com.nxin.framework.etl.designer.vo.log.JobLogVo;
import com.nxin.framework.etl.designer.vo.log.StepLogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.nxin.framework.etl.designer.enums.Constant.EXCEPTION_NOT_FOUNT;
import static com.nxin.framework.etl.designer.enums.Constant.EXCEPTION_UNAUTHORIZED;


@RestController
@RequestMapping("/history")
public class HistoryLogController {

    @Autowired
    private LogService logService;
    @Autowired
    private ShellPublishService shellPublishService;
    @Autowired
    private UserService userService;
    @Autowired
    private ShellPublishLogService shellPublishLogService;

    private static final String CHANNEL_TYPE_STEP = "STEP";

    @PostMapping("/logs")
    public ResponseEntity<List<JobLogVo>> log(@RequestBody CrudDto crudDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        ShellPublish existed = shellPublishService.online(crudDto.getId(), loginUser.getTenant().getId());
        if (existed != null) {
            if (existed.getShell().getProject().getUsers().contains(loginUser)) {
                List<String> logChannelIds = shellPublishLogService.allByShellPublish(existed.getId()).stream().map(shellPublishLog -> shellPublishLog.getLogChannelId()).collect(Collectors.toList());
                return ResponseEntity.ok(logService.historyJobLog(logChannelIds));
            }
            return ResponseEntity.status(EXCEPTION_UNAUTHORIZED).build();
        }
        return ResponseEntity.status(EXCEPTION_NOT_FOUNT).build();
    }

    @GetMapping("/steps/{id}/{jobId}")
    public ResponseEntity<List<JobEntryLogVo>> steps(@PathVariable("id") long id, @PathVariable("jobId") int jobId, Principal principal) {
        User loginUser = userService.one(principal.getName());
        ShellPublish existed = shellPublishService.online(id, loginUser.getTenant().getId());
        if (existed != null) {
            if (existed.getShell().getProject().getUsers().contains(loginUser)) {
                return ResponseEntity.ok(logService.historyJobEntryLog(jobId));
            }
            return ResponseEntity.status(EXCEPTION_UNAUTHORIZED).build();
        }
        return ResponseEntity.status(EXCEPTION_NOT_FOUNT).build();
    }

    @GetMapping("/detail/{id}/{channelId}")
    public ResponseEntity<List<JobLogVo>> detail(@PathVariable("id") long id, @PathVariable("channelId") String channelId, Principal principal) {
        User loginUser = userService.one(principal.getName());
        ShellPublish existed = shellPublishService.online(id, loginUser.getTenant().getId());
        if (existed != null) {
            if (existed.getShell().getProject().getUsers().contains(loginUser)) {
                return ResponseEntity.ok(logService.historyJobLog(Arrays.asList(channelId)));
            }
            return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
        }
        return ResponseEntity.status(EXCEPTION_NOT_FOUNT).build();
    }

    @GetMapping("/step/{id}/{channelId}")
    public ResponseEntity<List<StepLogVo>> step(@PathVariable("id") long id, @PathVariable("channelId") String channelId, Principal principal) {
        User loginUser = userService.one(principal.getName());
        ShellPublish existed = shellPublishService.online(id, loginUser.getTenant().getId());
        if (existed != null) {
            if (existed.getShell().getProject().getUsers().contains(loginUser)) {
                List<ChannelLogVo> channels = logService.historyChannelLogs(channelId);
                List<StepLogVo> stepLogs = new ArrayList<>(0);
                for (ChannelLogVo channel : channels) {
                    if (CHANNEL_TYPE_STEP.equals(channel.getLoggingObjectType())) {
                        stepLogs.addAll(logService.historyStepLog(channel.getChannelId()));
                    }
                }
                return ResponseEntity.ok(stepLogs);
            }
            return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
        }
        return ResponseEntity.status(EXCEPTION_NOT_FOUNT).build();
    }
}
