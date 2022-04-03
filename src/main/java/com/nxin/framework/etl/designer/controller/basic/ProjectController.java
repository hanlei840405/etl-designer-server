package com.nxin.framework.etl.designer.controller.basic;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.converter.bean.base.ProjectConverter;
import com.nxin.framework.etl.designer.converter.bean.base.UserConverter;
import com.nxin.framework.etl.designer.dto.CrudDto;
import com.nxin.framework.etl.designer.dto.basic.ProjectDto;
import com.nxin.framework.etl.designer.entity.basic.Project;
import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.service.basic.ProjectService;
import com.nxin.framework.etl.designer.service.basic.UserService;
import com.nxin.framework.etl.designer.vo.basic.ProjectVo;
import com.nxin.framework.etl.designer.vo.basic.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;

    private BeanConverter<ProjectVo, Project> projectConverter = new ProjectConverter();
    private BeanConverter<UserVo, User> userConverter = new UserConverter();

    @PreAuthorize("hasAuthority('ROOT') or hasAuthority('PROJECT')")
    @GetMapping("/project/{id}")
    public ResponseEntity<ProjectVo> one(@PathVariable Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Project project = projectService.one(id, loginUser.getTenant().getId());
        if (project != null && project.getUsers().contains(loginUser)) {
            return ResponseEntity.ok(projectConverter.convert(project, true));
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PreAuthorize("hasAuthority('ROOT') or hasAuthority('PROJECT')")
    @PostMapping("/projects")
    public ResponseEntity<List<ProjectVo>> projects(@RequestBody CrudDto crudDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        List<ProjectVo> projectsVo = projectConverter.convert(projectService.search(crudDto.getPayload(), loginUser.getId(), loginUser.getTenant().getId()));
        return ResponseEntity.ok(projectsVo);
    }

    @PreAuthorize("hasAuthority('ROOT') or hasAuthority('PROJECT')")
    @GetMapping("/project/members/{id}")
    public ResponseEntity<List<UserVo>> members(@PathVariable Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Project project = projectService.one(id, loginUser.getTenant().getId());
        if (project != null && project.getUsers().contains(loginUser)) {
            List<UserVo> usersVo = userConverter.convert(project.getUsers());
            return ResponseEntity.ok(usersVo);
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PreAuthorize("hasAuthority('ROOT') or hasAuthority('PROJECT')")
    @PostMapping("/project")
    public ResponseEntity<ProjectVo> save(@RequestBody ProjectDto project, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Project persisted;
        if (project.getId() != null) {
            persisted = projectService.one(project.getId(), loginUser.getTenant().getId());
            if (persisted != null && persisted.getUsers().contains(loginUser)) {
                BeanUtils.copyProperties(project, persisted, "id", "tenant", "creator", "createTime", "status", "users");
                persisted.setModifier(principal.getName());
                persisted = projectService.save(persisted, loginUser.getTenant());
            } else {
                return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
            }
        } else {
            persisted = new Project();
            BeanUtils.copyProperties(project, persisted, "users");
            persisted.setStatus(Constant.ACTIVE);
            persisted.setCreator(principal.getName());
            persisted.getUsers().add(loginUser);
            persisted = projectService.save(persisted, loginUser.getTenant());
        }
        return ResponseEntity.ok(projectConverter.convert(persisted, false));
    }

    @PreAuthorize("hasAuthority('ROOT') or hasAuthority('PROJECT')")
    @DeleteMapping("/project/{id}")
    public ResponseEntity<ProjectVo> delete(@PathVariable("id") Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Project persisted = projectService.one(id, loginUser.getTenant().getId());
        if (persisted != null && persisted.getUsers().contains(loginUser)) {
            persisted.setModifier(principal.getName());
            persisted.setStatus(Constant.INACTIVE);
            persisted = projectService.delete(persisted);
            return ResponseEntity.ok(projectConverter.convert(persisted, false));
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PreAuthorize("hasAuthority('ROOT') or hasAuthority('MEMBER')")
    @DeleteMapping("/project/member/{projectId}}")
    public ResponseEntity<List<UserVo>> quitProject(@PathVariable("projectId") Long projectId, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Project persisted = projectService.one(projectId, loginUser.getTenant().getId());
        if (persisted != null && persisted.getUsers().contains(loginUser)) {
            persisted.setModifier(principal.getName());
            persisted.setStatus(Constant.ACTIVE);
            persisted.getUsers().remove(loginUser);
            persisted = projectService.save(persisted, loginUser.getTenant());
            List<UserVo> usersVo = userConverter.convert(persisted.getUsers());
            return ResponseEntity.ok(usersVo);
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PreAuthorize("hasAuthority('ROOT') or hasAuthority('MEMBER')")
    @DeleteMapping("/project/member/{projectId}/{userId}")
    public ResponseEntity<List<UserVo>> deleteMember(@PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Project persisted = projectService.one(projectId, loginUser.getTenant().getId());
        if (persisted != null && persisted.getUsers().contains(loginUser)) {
            User user = userService.one(userId, loginUser.getTenant().getId());
            if (user != null && user.getTenant().equals(loginUser.getTenant())) {
                persisted.setModifier(principal.getName());
                persisted.setStatus(Constant.ACTIVE);
                persisted.getUsers().remove(user);
                persisted = projectService.save(persisted, loginUser.getTenant());
                List<UserVo> usersVo = userConverter.convert(persisted.getUsers());
                return ResponseEntity.ok(usersVo);
            }
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PreAuthorize("hasAuthority('ROOT') or hasAuthority('MEMBER')")
    @PostMapping("/project/member/{projectId}/{userId}")
    public ResponseEntity<List<UserVo>> saveMember(@PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Project persisted = projectService.one(projectId, loginUser.getTenant().getId());
        if (persisted != null && persisted.getUsers().contains(loginUser)) {
            User user = userService.one(userId, loginUser.getTenant().getId());
            if (user != null && user.getTenant().equals(loginUser.getTenant())) {
                persisted.setModifier(principal.getName());
                persisted.setStatus(Constant.ACTIVE);
                if (!persisted.getUsers().contains(user)) {
                    persisted.getUsers().add(user);
                    persisted = projectService.save(persisted, loginUser.getTenant());
                }
                List<UserVo> usersVo = userConverter.convert(persisted.getUsers());
                return ResponseEntity.ok(usersVo);
            }
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }
}
