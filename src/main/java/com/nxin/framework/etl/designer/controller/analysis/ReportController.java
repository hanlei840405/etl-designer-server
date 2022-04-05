package com.nxin.framework.etl.designer.controller.analysis;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.converter.bean.analysis.ReportConverter;
import com.nxin.framework.etl.designer.dto.CrudDto;
import com.nxin.framework.etl.designer.dto.analysis.ReportDimensionDto;
import com.nxin.framework.etl.designer.dto.analysis.ReportDto;
import com.nxin.framework.etl.designer.entity.analysis.Model;
import com.nxin.framework.etl.designer.entity.analysis.Report;
import com.nxin.framework.etl.designer.entity.analysis.ReportDimension;
import com.nxin.framework.etl.designer.entity.basic.Project;
import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.service.DynamicQueryDataService;
import com.nxin.framework.etl.designer.service.analysis.ModelService;
import com.nxin.framework.etl.designer.service.analysis.ReportService;
import com.nxin.framework.etl.designer.service.basic.ProjectService;
import com.nxin.framework.etl.designer.service.basic.UserService;
import com.nxin.framework.etl.designer.vo.analysis.ReportVo;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleValueException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PreAuthorize("hasAuthority('ROOT') or hasAuthority('REPORT')")
@RestController
@RequestMapping
public class ReportController {
    @Autowired
    private ReportService reportService;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelService modelService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private DynamicQueryDataService dynamicQueryDataService;

    private final BeanConverter<ReportVo, Report> reportConverter = new ReportConverter();

    @GetMapping("/report/{id}")
    public ResponseEntity<ReportVo> one(@PathVariable Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Report report = reportService.one(id, loginUser.getTenant().getId());
        if (report != null && report.getProject() != null && loginUser.getProjects().contains(report.getProject())) {
            report.setReportDimensions(report.getReportDimensions().stream().filter(reportDimension -> Constant.ACTIVE.equals(reportDimension.getStatus())).collect(Collectors.toList()));
            return ResponseEntity.ok(reportConverter.convert(report, true));
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PostMapping("/reports")
    public ResponseEntity<List<ReportVo>> reports(@RequestBody CrudDto crudDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Project project = projectService.one(crudDto.getId(), loginUser.getTenant().getId());
        if (project != null && project.getUsers().contains(loginUser)) {
            List<ReportVo> reportVoList = reportConverter.convert(reportService.search(project.getId(), crudDto.getPayload(), loginUser.getTenant().getId()));
            return ResponseEntity.ok(reportVoList);
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PostMapping("/reports/show")
    public ResponseEntity<List<ReportVo>> show(@RequestBody CrudDto crudDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        List<ReportVo> reportVoList = reportConverter.convert(reportService.show(crudDto.getPayload(), loginUser.getTenant().getId()));
        return ResponseEntity.ok(reportVoList);
    }

    @PostMapping("/reports/deploy")
    public ResponseEntity<Map<String, Object>> deploy(@RequestBody CrudDto crudDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Report persisted = reportService.one(crudDto.getId(), loginUser.getTenant().getId());
        if (persisted != null) {
            try {
                Map<String, Object> result = dynamicQueryDataService.query(persisted.getModel().getDatasource().getName(), persisted.getModel().getDatasource().getCategory(), persisted.getModel().getDatasource().getHost(), persisted.getModel().getDatasource().getSchemaName(), persisted.getModel().getDatasource().getPort().toString(), persisted.getModel().getDatasource().getUsername(), persisted.getModel().getDatasource().getPassword(), persisted.getScript());
                return ResponseEntity.ok(result);
            } catch (KettleDatabaseException | KettleValueException e) {
                return ResponseEntity.status(Constant.EXCEPTION_SQL_GRAMMAR).build();
            }
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PostMapping("/report")
    public ResponseEntity<ReportVo> save(@RequestBody ReportDto reportDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Project project = projectService.one(reportDto.getProject().getId(), loginUser.getTenant().getId());
        if (project != null && project.getUsers().contains(loginUser)) {
            Model model = modelService.one(reportDto.getModel().getId(), loginUser.getTenant().getId());
            if (model != null && model.getProject().getUsers().contains(loginUser)) {
                Report persisted;
                if (reportDto.getId() != null) {
                    persisted = reportService.one(reportDto.getId(), loginUser.getTenant().getId());
                    if (persisted != null) {
                        BeanUtils.copyProperties(reportDto, persisted, "id", "model", "project", "creator", "createTime", "status");
                        persisted.setModel(model);
                        persisted.setProject(project);
                        persisted.setModifier(principal.getName());
                        persisted = reportService.save(persisted, convert(reportDto.getReportDimensions()), loginUser.getTenant());
                        return ResponseEntity.ok(reportConverter.convert(persisted, false));
                    }
                    return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
                }
                persisted = new Report();
                BeanUtils.copyProperties(reportDto, persisted, "id", "model", "project");
                persisted.setProject(project);
                persisted.setModel(model);
                persisted.setStatus(Constant.ACTIVE);
                persisted.setCreator(principal.getName());
                persisted = reportService.save(persisted, convert(reportDto.getReportDimensions()), loginUser.getTenant());
                return ResponseEntity.ok(reportConverter.convert(persisted, false));
            }
        }

        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @DeleteMapping("/report/{id}")
    public ResponseEntity<ReportVo> delete(@PathVariable("id") Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        Report persisted = reportService.one(id, loginUser.getTenant().getId());
        if (persisted != null && persisted.getProject() != null && loginUser.getProjects().contains(persisted.getProject())) {
            persisted.setModifier(principal.getName());
            persisted.setStatus(Constant.INACTIVE);
            persisted = reportService.delete(persisted);
            return ResponseEntity.ok(reportConverter.convert(persisted, false));
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    private List<ReportDimension> convert(List<ReportDimensionDto> reportDimensionDtoList) {
        return reportDimensionDtoList.stream().map(reportDimensionDto -> {
            ReportDimension reportDimension = new ReportDimension();
            BeanUtils.copyProperties(reportDimensionDto, reportDimension, "id", "report");
            return reportDimension;
        }).collect(Collectors.toList());
    }
}
