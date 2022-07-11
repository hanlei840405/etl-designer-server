package com.nxin.framework.etl.designer.controller.designer;

import com.google.common.io.Files;
import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.converter.bean.designer.ShellConverter;
import com.nxin.framework.etl.designer.dto.CrudDto;
import com.nxin.framework.etl.designer.dto.designer.ShellDto;
import com.nxin.framework.etl.designer.entity.basic.Project;
import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.entity.designer.Shell;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.exception.RecordsNotMatchException;
import com.nxin.framework.etl.designer.service.basic.ProjectService;
import com.nxin.framework.etl.designer.service.basic.UserService;
import com.nxin.framework.etl.designer.service.designer.ShellPublishService;
import com.nxin.framework.etl.designer.service.designer.ShellService;
import com.nxin.framework.etl.designer.utils.ZipUtils;
import com.nxin.framework.etl.designer.vo.TreeNodeVo;
import com.nxin.framework.etl.designer.vo.designer.ShellVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class ShellController {
    @Autowired
    private ShellService shellService;
    @Autowired
    private ShellPublishService shellPublishService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    private BeanConverter<ShellVo, Shell> shellConverter = new ShellConverter();

    @GetMapping("/shell/{id}")
    public ResponseEntity<ShellVo> one(@PathVariable Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Shell persisted = shellService.one(id, loginUser.getTenant().getId());
        if (persisted != null && persisted.getProject().getUsers().contains(loginUser)) {
            ShellVo shellVo = shellConverter.convert(persisted, true);
            return ResponseEntity.ok(shellVo);
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @GetMapping("/shell/source/{id}")
    public ResponseEntity<String> source(@PathVariable Long id, Principal principal) throws IOException {
        User loginUser = userService.one(principal.getName());
        Shell persisted = shellService.one(id, loginUser.getTenant().getId());
        if (persisted != null && persisted.getProject().getUsers().contains(loginUser)) {
            String xml = Files.asCharSource(new File(persisted.getXml()), Charset.defaultCharset()).read();
            return ResponseEntity.ok(ZipUtils.compress(xml));
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PostMapping("/shells/{projectId}")
    public ResponseEntity<List<TreeNodeVo>> shells(@PathVariable Long projectId, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Project project = projectService.one(projectId, loginUser.getTenant().getId());
        if (project != null && project.getUsers().contains(loginUser)) {
            List<TreeNodeVo> treeNodes = shellService.all(projectId, loginUser.getTenant().getId()).stream().map(shell -> {
                TreeNodeVo treeNodeVo = TreeNodeVo.builder().category(shell.getCategory()).id(shell.getId()).label(shell.getName()).build();
                if (shell.getShell() != null) {
                    treeNodeVo.setParentId(shell.getShell().getId());
                } else {
                    treeNodeVo.setParentId(0L);
                }
                return treeNodeVo;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(treeNodes);
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PostMapping("/shell/publish")
    public ResponseEntity<ShellVo> publish(@RequestBody CrudDto payload, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Shell persisted = shellService.one(payload.getId(), loginUser.getTenant().getId());
        if (persisted != null && persisted.getProject().getUsers().contains(loginUser)) {
            if (persisted.isExecutable()) {
                try {
                    shellPublishService.save(persisted, payload.getPayload(), loginUser.getTenant());
                    ShellVo shellVo = shellConverter.convert(persisted, false);
                    return ResponseEntity.ok(shellVo);
                } catch (RecordsNotMatchException | IOException e) {
                    return ResponseEntity.status(Constant.EXCEPTION_RECORDS_NOT_MATCH).build();
                }
            }
            return ResponseEntity.status(Constant.EXCEPTION_ETL_GRAMMAR).build();
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PostMapping("/shell")
    public ResponseEntity<ShellVo> save(@RequestBody ShellDto shellDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        if (shellDto.getProject() != null && shellDto.getProject().getId() != null) {
            Project project = projectService.one(shellDto.getProject().getId(), loginUser.getTenant().getId());
            if (project != null && project.getUsers().contains(loginUser)) {
                Shell parent = null;
                if (shellDto.getShell() != null && shellDto.getShell().getId() != null && !Objects.equals(shellDto.getShell().getId(), shellDto.getId())) {
                    parent = shellService.one(shellDto.getShell().getId(), loginUser.getTenant().getId());
                }
                Shell persisted;
                if (shellDto.getId() != null) {
                    // 更新
                    persisted = shellService.one(shellDto.getId(), loginUser.getTenant().getId());
                    if (persisted != null && persisted.getProject() != null && persisted.getProject().getUsers().contains(loginUser)) {
                        BeanUtils.copyProperties(shellDto, persisted, "id", "createTime", "creator", "modifier", "xml", "shell", "project", "status");
                        persisted.setProject(project);
                        persisted.setShell(parent);
                        persisted.setModifier(principal.getName());
                    } else {
                        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
                    }
                } else {
                    // 新建
                    persisted = new Shell();
                    BeanUtils.copyProperties(shellDto, persisted, "creator", "modifier", "xml", "shell", "project");
                    persisted.setProject(project);
                    persisted.setShell(parent);
                    persisted.setModifier(principal.getName());
                    persisted.setStatus(Constant.ACTIVE);
                    persisted.setCreator(principal.getName());
                }
                persisted = shellService.save(persisted, loginUser.getTenant());
                return ResponseEntity.ok(shellConverter.convert(persisted, false));
            }
            return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
        } else {
            // 无分支的shell判定为无效数据
            return ResponseEntity.status(Constant.EXCEPTION_DATA).build();
        }

    }

    @PostMapping("/shell/content")
    public ResponseEntity<ShellVo> content(@RequestBody ShellDto shellDto, Principal principal) {
        if (shellDto.getId() != null) {
            User loginUser = userService.one(principal.getName());
            Shell persisted = shellService.one(shellDto.getId(), loginUser.getTenant().getId());
            if (persisted != null && persisted.getProject().getUsers().contains(loginUser)) {
                persisted.setContent(shellDto.getContent());
                persisted.setModifier(principal.getName());
                persisted = shellService.save(persisted, loginUser.getTenant());
                return ResponseEntity.ok(shellConverter.convert(persisted, false));
            }
            return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
        }
        return ResponseEntity.status(Constant.EXCEPTION_NOT_FOUNT).build();
    }

    @DeleteMapping("/shell/{id}")
    public ResponseEntity<ShellVo> delete(@PathVariable Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Shell persisted = shellService.one(id, loginUser.getTenant().getId());
        if (persisted != null && persisted.getProject().getUsers().contains(loginUser)) {
            persisted.setStatus(Constant.INACTIVE);
            persisted.setModifier(principal.getName());
            persisted = shellService.save(persisted, loginUser.getTenant());
            return ResponseEntity.ok(shellConverter.convert(persisted, false));
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }
}
