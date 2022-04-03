package com.nxin.framework.etl.designer.controller.designer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.converter.bean.designer.ShellPublishConverter;
import com.nxin.framework.etl.designer.dto.CrudDto;
import com.nxin.framework.etl.designer.entity.basic.Project;
import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.entity.designer.Shell;
import com.nxin.framework.etl.designer.entity.designer.ShellPublish;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.service.basic.ProjectService;
import com.nxin.framework.etl.designer.service.basic.UserService;
import com.nxin.framework.etl.designer.service.designer.ShellPublishService;
import com.nxin.framework.etl.designer.service.designer.ShellService;
import com.nxin.framework.etl.designer.vo.PageVo;
import com.nxin.framework.etl.designer.vo.designer.ShellPublishVo;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class ShellPublishController {
    @Autowired
    private ShellService shellService;
    @Autowired
    private ShellPublishService shellPublishService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;
    private ObjectMapper objectMapper = new ObjectMapper();
    private BeanConverter<ShellPublishVo, ShellPublish> shellPublishConverter = new ShellPublishConverter();

    @PostMapping("/publishes")
    public ResponseEntity<PageVo<ShellPublishVo>> shells(@RequestBody CrudDto crudDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Shell shell = shellService.one(crudDto.getId(), loginUser.getTenant().getId());
        if (shell == null || shell.getProject() == null || shell.getProject().getUsers().isEmpty()) {
            return ResponseEntity.status(Constant.EXCEPTION_NOT_FOUNT).build();
        }
        if (shell.getProject().getUsers().contains(loginUser)) {
            PageVo<ShellPublish> shellPublishPageVo = shellPublishService.findHistories(crudDto.getId(), loginUser.getTenant().getId(), crudDto.getPageNo(), crudDto.getPageSize());
            PageVo<ShellPublishVo> shellPublishVoPageVo = new PageVo<>(shellPublishPageVo.getTotal(), shellPublishConverter.convert(shellPublishPageVo.getItems()));
            return ResponseEntity.ok(shellPublishVoPageVo);
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PostMapping("/online")
    public ResponseEntity<PageVo<ShellPublishVo>> online(@RequestBody CrudDto crudDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        PageVo<ShellPublish> shellPublishPageVo = shellPublishService.online(crudDto.getPayload(), projectService.search(null, loginUser.getId(), loginUser.getTenant().getId()).stream().mapToLong(Project::getId).toArray(), loginUser.getTenant().getId(), crudDto.getPageNo(), crudDto.getPageSize());
        PageVo<ShellPublishVo> shellPublishVoPageVo = new PageVo<>(shellPublishPageVo.getTotal(), shellPublishConverter.convert(shellPublishPageVo.getItems()));
        return ResponseEntity.ok(shellPublishVoPageVo);
    }

    @PostMapping("/referencePublishes")
    public ResponseEntity<List<ShellPublishVo>> referencePublishes(@RequestBody CrudDto crudDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        ShellPublish shellPublish = shellPublishService.one(crudDto.getId());
        if (shellPublish != null && shellPublish.getShell() != null && shellPublish.getShell().getProject() != null && shellPublish.getShell().getProject().getUsers().contains(loginUser)) {
            List<ShellPublish> records = shellPublishService.references(shellPublish, loginUser.getTenant().getId());
            records.add(shellPublish);
            return ResponseEntity.ok(shellPublishConverter.convert(records));
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    /**
     * 上线新版并下线旧版
     *
     * @param crudDto
     */
    @PostMapping("/deploy")
    public ResponseEntity<ShellPublishVo> deploy(@RequestBody CrudDto crudDto, Principal principal) throws JsonProcessingException {
        User loginUser = userService.one(principal.getName());
        Map<String, Object> payload = objectMapper.readValue(crudDto.getPayload(), new TypeReference<Map<String, Object>>() {
        });
        ShellPublish persisted = shellPublishService.one(crudDto.getId(), loginUser.getTenant().getId());
        if (persisted != null) {
            if (persisted.getShell().getProject().getUsers().contains(loginUser)) {
                persisted.setProd(Constant.ACTIVE);
                persisted.setDeployTime(new Date());
                try {
                    if (Constant.BATCH.equals(persisted.getStreaming())) {
                        shellPublishService.deploySchedule(persisted, (String) payload.get("cron"), (int) payload.get("misfire"));
                    } else {
                        shellPublishService.deployStreaming(persisted);
                    }
                } catch (SchedulerException e) {
                    return ResponseEntity.status(Constant.EXCEPTION_ADD_SCHEDULE).build();
                } catch (Exception e) {
                    return ResponseEntity.status(Constant.EXCEPTION_DATA).build();
                }
                return ResponseEntity.ok(shellPublishConverter.convert(persisted, false));
            }
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }
}
