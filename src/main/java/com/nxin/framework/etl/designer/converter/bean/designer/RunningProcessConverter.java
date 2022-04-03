package com.nxin.framework.etl.designer.converter.bean.designer;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.entity.designer.RunningProcess;
import com.nxin.framework.etl.designer.entity.designer.Shell;
import com.nxin.framework.etl.designer.entity.designer.ShellPublish;
import com.nxin.framework.etl.designer.vo.designer.RunningProcessVo;
import com.nxin.framework.etl.designer.vo.designer.ShellPublishVo;
import com.nxin.framework.etl.designer.vo.designer.ShellVo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class RunningProcessConverter extends BeanConverter<RunningProcessVo, RunningProcess> {

    @Override
    public RunningProcessVo convert(RunningProcess runningProcess, boolean deep) {
        BeanConverter<ShellVo, Shell> shellConverter = new ShellConverter();
        BeanConverter<ShellPublishVo, ShellPublish> shellPublishConverter = new ShellPublishConverter();
        RunningProcessVo runningProcessVo = new RunningProcessVo();
        BeanUtils.copyProperties(runningProcess, runningProcessVo, "shell", "shellPublish");
        if (runningProcess.getShell() != null) {
            runningProcessVo.setShell(shellConverter.convert(runningProcess.getShell(), false));
        }
        if (runningProcess.getShell() != null) {
            runningProcessVo.setShellPublish(shellPublishConverter.convert(runningProcess.getShellPublish(), false));
        }
        return runningProcessVo;
    }

    @Override
    public List<RunningProcessVo> convert(List<RunningProcess> runningProcesses) {
        return runningProcesses.stream().map(item -> convert(item, false)).collect(Collectors.toList());
    }
}
