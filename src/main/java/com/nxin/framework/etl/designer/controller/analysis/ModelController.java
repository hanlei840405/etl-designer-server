package com.nxin.framework.etl.designer.controller.analysis;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.converter.bean.analysis.ModelConverter;
import com.nxin.framework.etl.designer.dto.CrudDto;
import com.nxin.framework.etl.designer.dto.analysis.MetadataDto;
import com.nxin.framework.etl.designer.dto.analysis.ModelDto;
import com.nxin.framework.etl.designer.entity.analysis.Metadata;
import com.nxin.framework.etl.designer.entity.analysis.Model;
import com.nxin.framework.etl.designer.entity.basic.Project;
import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.entity.designer.Datasource;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.service.DynamicQueryDataService;
import com.nxin.framework.etl.designer.service.analysis.MetadataService;
import com.nxin.framework.etl.designer.service.analysis.ModelService;
import com.nxin.framework.etl.designer.service.basic.ProjectService;
import com.nxin.framework.etl.designer.service.basic.UserService;
import com.nxin.framework.etl.designer.service.designer.DatasourceService;
import com.nxin.framework.etl.designer.vo.analysis.ModelVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@PreAuthorize("hasAuthority('ROOT') or hasAuthority('MODEL')")
@RestController
@RequestMapping
public class ModelController {
    @Autowired
    private ModelService modelService;
    @Autowired
    private UserService userService;
    @Autowired
    private DatasourceService datasourceService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private DynamicQueryDataService dynamicQueryDataService;
    @Autowired
    private MetadataService metadataService;

    private BeanConverter<ModelVo, Model> modelConverter = new ModelConverter();

    @GetMapping("/model/{id}")
    public ResponseEntity<ModelVo> one(@PathVariable Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Model model = modelService.one(id, loginUser.getTenant().getId());
        if (model != null && model.getProject() != null && loginUser.getProjects().contains(model.getProject())) {
            model.setMetadataList(model.getMetadataList().stream().filter(metadata -> Constant.ACTIVE.equals(metadata.getStatus())).collect(Collectors.toList()));
            return ResponseEntity.ok(modelConverter.convert(model, true));
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PostMapping("/models")
    public ResponseEntity<List<ModelVo>> models(@RequestBody CrudDto crudDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Project project = projectService.one(crudDto.getId(), loginUser.getTenant().getId());
        if (project != null && project.getUsers().contains(loginUser)) {
            List<ModelVo> modelVoList = modelConverter.convert(modelService.search(project.getId(), crudDto.getPayload(), loginUser.getTenant().getId()));
            return ResponseEntity.ok(modelVoList);
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PostMapping("/model")
    public ResponseEntity<ModelVo> save(@RequestBody ModelDto modelDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Project project = projectService.one(modelDto.getProject().getId(), loginUser.getTenant().getId());
        if (project != null && project.getUsers().contains(loginUser)) {
            Datasource datasource = datasourceService.one(modelDto.getDatasource().getId(), loginUser.getTenant().getId());
            if (datasource != null && datasource.getProject() != null && loginUser.getProjects().contains(datasource.getProject())) {
                Model persisted;
                if (modelDto.getId() != null) {
                    persisted = modelService.one(modelDto.getId(), loginUser.getTenant().getId());
                    if (persisted != null) {
                        BeanUtils.copyProperties(modelDto, persisted, "id", "project", "datasource", "creator", "createTime", "status");
                        persisted.setProject(project);
                        persisted.setDatasource(datasource);
                        persisted.setModifier(principal.getName());
                        persisted = modelService.save(persisted, convert(modelDto.getMetadataList()), loginUser.getTenant());
                        return ResponseEntity.ok(modelConverter.convert(persisted, false));
                    }
                }
                persisted = new Model();
                BeanUtils.copyProperties(modelDto, persisted, "id", "project", "datasource");
                persisted.setProject(project);
                persisted.setDatasource(datasource);
                persisted.setStatus(Constant.ACTIVE);
                persisted.setCreator(principal.getName());
                persisted = modelService.save(persisted, convert(modelDto.getMetadataList()), loginUser.getTenant());
                return ResponseEntity.ok(modelConverter.convert(persisted, false));
            }
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @DeleteMapping("/model/{id}")
    public ResponseEntity<ModelVo> delete(@PathVariable("id") Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Model persisted = modelService.one(id, loginUser.getTenant().getId());
        if (persisted != null && persisted.getProject() != null && loginUser.getProjects().contains(persisted.getProject())) {
            persisted.setModifier(principal.getName());
            persisted.setStatus(Constant.INACTIVE);
            persisted = modelService.delete(persisted);
            return ResponseEntity.ok(modelConverter.convert(persisted, false));
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @GetMapping("/generateSql/{id}")
    public ResponseEntity<String> generateSql(@PathVariable("id") Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Model persisted = modelService.one(id, loginUser.getTenant().getId());
        if (persisted != null && persisted.getProject() != null && loginUser.getProjects().contains(persisted.getProject())) {
            List<Metadata> metadataList = persisted.getMetadataList().stream().filter(metadata -> metadata.getStatus().equals(Constant.ACTIVE)).collect(Collectors.toList());
            return ResponseEntity.ok(metadataService.generateSql(persisted.getCode(), metadataList));
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @GetMapping("/executeSql/{id}")
    public ResponseEntity<Boolean> executeSql(@PathVariable("id") Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Model persisted = modelService.one(id, loginUser.getTenant().getId());
        if (persisted != null && persisted.getProject() != null && loginUser.getProjects().contains(persisted.getProject())) {
            if (dynamicQueryDataService.exist(persisted.getDatasource().getName(), persisted.getDatasource().getCategory(), persisted.getDatasource().getHost(), persisted.getDatasource().getSchemaName(), persisted.getDatasource().getPort().toString(), persisted.getDatasource().getUsername(), persisted.getDatasource().getPassword(), persisted.getCode())) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss");
                dynamicQueryDataService.execute(persisted.getDatasource().getName(), persisted.getDatasource().getCategory(), persisted.getDatasource().getHost(), persisted.getDatasource().getSchemaName(), persisted.getDatasource().getPort().toString(), persisted.getDatasource().getUsername(), persisted.getDatasource().getPassword(), "ALTER TABLE `" + persisted.getCode() + "` RENAME TO  `" + persisted.getCode() + simpleDateFormat.format(new Date()) + "` ;");
            }
            List<Metadata> metadataList = persisted.getMetadataList().stream().filter(metadata -> metadata.getStatus().equals(Constant.ACTIVE)).collect(Collectors.toList());
            return ResponseEntity.ok(dynamicQueryDataService.execute(persisted.getDatasource().getName(), persisted.getDatasource().getCategory(), persisted.getDatasource().getHost(), persisted.getDatasource().getSchemaName(), persisted.getDatasource().getPort().toString(), persisted.getDatasource().getUsername(), persisted.getDatasource().getPassword(), metadataService.generateSql(persisted.getCode(), metadataList)));
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    private List<Metadata> convert(List<MetadataDto> metadataDtoList) {
        List<Metadata> metadataList = metadataDtoList.stream().map(metadataDto -> {
            Metadata metadata = new Metadata();
            BeanUtils.copyProperties(metadataDto, metadata, "id", "model");
            return metadata;
        }).collect(Collectors.toList());
        return metadataList;
    }
}
