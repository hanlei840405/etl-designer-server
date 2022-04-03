package com.nxin.framework.etl.designer.controller.designer;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.converter.bean.designer.DatasourceConverter;
import com.nxin.framework.etl.designer.dto.CrudDto;
import com.nxin.framework.etl.designer.dto.designer.DatasourceDto;
import com.nxin.framework.etl.designer.entity.basic.Project;
import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.entity.designer.Datasource;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.enums.DatasourceType;
import com.nxin.framework.etl.designer.service.basic.ProjectService;
import com.nxin.framework.etl.designer.service.basic.UserService;
import com.nxin.framework.etl.designer.service.designer.DatasourceService;
import com.nxin.framework.etl.designer.vo.designer.DatasourceVo;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.database.DatabaseTestResults;
import org.pentaho.di.core.encryption.Encr;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@PreAuthorize("hasAuthority('ROOT') or hasAuthority('DATASOURCE')")
@RestController
@RequestMapping
public class DatasourceController {
    @Autowired
    private DatasourceService datasourceService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    private BeanConverter<DatasourceVo, Datasource> datasourceConverter = new DatasourceConverter();

    @GetMapping("/datasource/{id}")
    public ResponseEntity<DatasourceVo> one(@PathVariable Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Datasource datasource = datasourceService.one(id, loginUser.getTenant().getId());
        if (datasource != null && datasource.getProject() != null && loginUser.getProjects().contains(datasource.getProject())) {
            return ResponseEntity.ok(datasourceConverter.convert(datasource, true));
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PostMapping("/datasourceList")
    public ResponseEntity<List<DatasourceVo>> list(@RequestBody CrudDto crudDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Project project = projectService.one(crudDto.getId(), loginUser.getTenant().getId());
        if (project != null && project.getUsers().contains(loginUser)) {
            if (crudDto.getIgnoreStatus()) {
                return ResponseEntity.ok(datasourceConverter.convert(datasourceService.all(crudDto.getId(), loginUser.getTenant().getId())));
            }
            return ResponseEntity.ok(datasourceConverter.convert(datasourceService.all(crudDto.getId(), Constant.ACTIVE, loginUser.getTenant().getId())));
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PostMapping("/datasource")
    public ResponseEntity<DatasourceVo> save(@RequestBody DatasourceDto datasourceDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        if (datasourceDto.getProject() != null && datasourceDto.getProject().getId() != null) {
            Project project = projectService.one(datasourceDto.getProject().getId(), loginUser.getTenant().getId());
            if (project != null && project.getUsers().contains(loginUser)) {
                Datasource persisted;
                if (datasourceDto.getId() != null) {
                    persisted = datasourceService.one(datasourceDto.getId(), loginUser.getTenant().getId());
                    if (persisted == null || persisted.getProject() == null) {
                        return ResponseEntity.status(Constant.EXCEPTION_NOT_FOUNT).build();
                    }
                    if (persisted.getProject().getUsers().contains(loginUser)) {
                        BeanUtils.copyProperties(datasourceDto, persisted, "createTime", "creator", "project", "status");
                        persisted.setProject(project);
                        persisted.setModifier(principal.getName());
                        persisted = datasourceService.save(persisted, loginUser.getTenant());
                        return ResponseEntity.ok(datasourceConverter.convert(persisted, false));
                    }
                } else {
                    persisted = new Datasource();
                    BeanUtils.copyProperties(datasourceDto, persisted, "project");
                    persisted.setProject(project);
                    persisted.setStatus(Constant.ACTIVE);
                    persisted.setCreator(principal.getName());
                    persisted = datasourceService.save(persisted, loginUser.getTenant());
                    return ResponseEntity.ok(datasourceConverter.convert(persisted, false));
                }
            }
            return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
        } else {
            return ResponseEntity.status(Constant.EXCEPTION_DATA).build();
        }
    }

    @PostMapping("/datasource/test")
    public ResponseEntity<Boolean> test(@RequestBody Datasource datasource) {
        DatabaseMeta databaseMeta = new DatabaseMeta(datasource.getName(), DatasourceType.getValue(datasource.getCategory()), null, datasource.getHost(), datasource.getSchemaName(), datasource.getPort().toString(), datasource.getUsername(), Constant.PASSWORD_ENCRYPTED_PREFIX + Encr.encryptPassword(datasource.getPassword()));
        DatabaseTestResults databaseTestResults = databaseMeta.testConnectionSuccess();
        return ResponseEntity.ok(databaseTestResults.isSuccess());
    }

    @DeleteMapping("/datasource/{id}")
    public ResponseEntity<DatasourceVo> delete(@PathVariable Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Datasource persisted = datasourceService.one(id, loginUser.getTenant().getId());
        if (persisted != null && persisted.getProject() != null && persisted.getProject().getUsers().contains(loginUser)) {
            persisted.setStatus(Constant.INACTIVE);
            persisted.setModifier(principal.getName());
            persisted = datasourceService.save(persisted, loginUser.getTenant());
            return ResponseEntity.ok(datasourceConverter.convert(persisted, false));
        } else {
            return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
        }
    }
}
