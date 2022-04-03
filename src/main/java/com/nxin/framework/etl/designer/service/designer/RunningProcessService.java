package com.nxin.framework.etl.designer.service.designer;

import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.entity.designer.RunningProcess;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.repository.designer.RunningProcessRepository;
import com.nxin.framework.etl.designer.vo.PageVo;
import org.pentaho.di.www.CarteObjectEntry;
import org.pentaho.di.www.CarteSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RunningProcessService {
    @Autowired
    private RunningProcessRepository runningProcessRepository;

    public RunningProcess one(Long id, Long tenantId) {
        return runningProcessRepository.getFirstByIdAndTenantId(id, tenantId);
    }

    public List<RunningProcess> instanceId(String instanceId, Long tenantId) {
        return runningProcessRepository.findAllByInstanceIdAndTenantId(instanceId, tenantId);
    }

    public PageVo<RunningProcess> page(Long userId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<RunningProcess> pageRecord = runningProcessRepository.findByShellProjectUsersIdOrShellPublishShellProjectUsersId(userId, userId, pageable);
        return new PageVo(pageRecord.getTotalElements(), pageRecord.getContent());
    }

    public RunningProcess save(RunningProcess runningProcess, Tenant tenant) {
        runningProcess.setTenant(tenant);
        return runningProcessRepository.save(runningProcess);
    }

    public void delete(RunningProcess runningProcess) {
        if (Constant.JOB.equals(runningProcess.getCategory())) {
            CarteSingleton.getInstance().getJobMap().removeJob(new CarteObjectEntry(runningProcess.getInstanceName(), runningProcess.getInstanceId()));
        } else {
            CarteSingleton.getInstance().getTransformationMap().removeTransformation(new CarteObjectEntry(runningProcess.getInstanceName(), runningProcess.getInstanceId()));
        }
        runningProcessRepository.delete(runningProcess);
    }

    public void delete(List<RunningProcess> runningProcessList) {
        runningProcessRepository.deleteInBatch(runningProcessList);
    }
}
