package com.nxin.framework.etl.designer.controller.designer;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.converter.bean.designer.RunningProcessConverter;
import com.nxin.framework.etl.designer.dto.CrudDto;
import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.entity.designer.RunningProcess;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.service.basic.UserService;
import com.nxin.framework.etl.designer.service.designer.RunningProcessService;
import com.nxin.framework.etl.designer.vo.PageVo;
import com.nxin.framework.etl.designer.vo.designer.RunningProcessVo;
import org.pentaho.di.job.Job;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.www.CarteObjectEntry;
import org.pentaho.di.www.CarteSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@PreAuthorize("hasAuthority('ROOT') or hasAuthority('PROCESS')")
@RestController
@RequestMapping
public class RunningProcessController {
    @Autowired
    private RunningProcessService runningProcessService;
    @Autowired
    private UserService userService;
    private BeanConverter<RunningProcessVo, RunningProcess> runningProcessConverter = new RunningProcessConverter();

    @PreAuthorize("hasAuthority('ROOT') or hasAuthority('PROCESS')")
    @PostMapping("/runningProcesses")
    public ResponseEntity<PageVo<RunningProcessVo>> runningProcesses(@RequestBody CrudDto crudDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        PageVo<RunningProcess> runningProcessPageVo = runningProcessService.page(loginUser.getId(), crudDto.getPageNo(), crudDto.getPageSize());
        PageVo<RunningProcessVo> runningProcessVoPageVo = new PageVo<>(runningProcessPageVo.getTotal(), runningProcessConverter.convert(runningProcessPageVo.getItems()));
        return ResponseEntity.ok(runningProcessVoPageVo);
    }

    @PreAuthorize("hasAuthority('ROOT') or hasAuthority('PROCESS')")
    @DeleteMapping("/runningProcess/{id}")
    public ResponseEntity<RunningProcessVo> delete(@PathVariable Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        RunningProcess runningProcess = runningProcessService.one(id, loginUser.getTenant().getId());
        if (runningProcess == null) {
            return ResponseEntity.status(Constant.EXCEPTION_NOT_FOUNT).build();
        }
        if ("1".equals(runningProcess.getProd()) && (runningProcess.getShellPublish() == null || runningProcess.getShellPublish().getShell() == null || runningProcess.getShellPublish().getShell().getProject() == null)) {
            return ResponseEntity.status(Constant.EXCEPTION_NOT_FOUNT).build();
        } else if ("0".equals(runningProcess.getProd()) && (runningProcess.getShell() == null || runningProcess.getShell().getProject() == null)) {
            return ResponseEntity.status(Constant.EXCEPTION_NOT_FOUNT).build();
        } else if (!loginUser.getProjects().contains(runningProcess.getShell().getProject())) {
            return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
        } else {
            CarteObjectEntry carteObjectEntry = new CarteObjectEntry(principal.getName(), runningProcess.getInstanceId());
            if (Constant.JOB.equals(runningProcess.getCategory()) && CarteSingleton.getInstance().getJobMap().getJobObjects().contains(carteObjectEntry)) {
                Job job = CarteSingleton.getInstance().getJobMap().getJob(carteObjectEntry);
                job.stopAll();
            } else if (Constant.TRANSFORM.equals(runningProcess.getCategory()) && CarteSingleton.getInstance().getTransformationMap().getTransformationObjects().contains(carteObjectEntry)) {
                Trans trans = CarteSingleton.getInstance().getTransformationMap().getTransformation(carteObjectEntry);
                trans.stopAll();
            }
            runningProcessService.delete(runningProcess);
            RunningProcessVo runningProcessVo = runningProcessConverter.convert(runningProcess, false);
            return ResponseEntity.ok(runningProcessVo);
        }
    }

//    @PreAuthorize("hasAuthority('ROOT') or hasAuthority('PROBATION')")
//    @DeleteMapping("/runningProcess/instanceId/{instanceId}")
//    public ResponseEntity<List<RunningProcess>> deleteByInstanceId(@PathVariable String instanceId, Principal principal) {
//        User loginUser = userService.one(principal.getName());
//        List<RunningProcess> runningProcessList = runningProcessService.instanceId(instanceId);
//        if (!runningProcessList.isEmpty() && runningProcessList.get(0) != null && (runningProcessList.get(0).getShell() != null && runningProcessList.get(0).getShell().getProject() != null
//                && loginUser.getProjects().contains(runningProcessList.get(0).getShell().getProject())) || runningProcessList.get(0).getShellPublish() != null
//                && runningProcessList.get(0).getShellPublish().getShell() != null && runningProcessList.get(0).getShellPublish().getShell().getProject() != null
//                && loginUser.getProjects().contains(runningProcessList.get(0).getShellPublish().getShell().getProject())
//        ) {
//            CarteObjectEntry carteObjectEntry = new CarteObjectEntry(runningProcessList.get(0).getInstanceName(), instanceId);
//            if (Constant.JOB.equals(runningProcessList.get(0).getCategory()) && CarteSingleton.getInstance().getJobMap().getJobObjects().contains(carteObjectEntry)) {
//                Job job = CarteSingleton.getInstance().getJobMap().getJob(carteObjectEntry);
//                job.stopAll();
//            } else if (Constant.TRANSFORM.equals(runningProcessList.get(0).getCategory()) && CarteSingleton.getInstance().getTransformationMap().getTransformationObjects().contains(carteObjectEntry)) {
//                Trans trans = CarteSingleton.getInstance().getTransformationMap().getTransformation(carteObjectEntry);
//                trans.stopAll();
//            }
//            runningProcessService.delete(runningProcessList);
//            return ResponseEntity.ok(runningProcessList);
//        } else {
//            return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
//        }
//    }
}
