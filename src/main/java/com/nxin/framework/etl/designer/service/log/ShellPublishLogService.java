package com.nxin.framework.etl.designer.service.log;

import com.nxin.framework.etl.designer.entity.log.ShellPublishLog;
import com.nxin.framework.etl.designer.repository.log.ShellPublishLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShellPublishLogService {
    @Autowired
    private ShellPublishLogRepository shellPublishLogRepository;

    public List<ShellPublishLog> allByShellPublish(Long shellPublishId) {
        return shellPublishLogRepository.findByShellPublishId(shellPublishId);
    }

    public void save(ShellPublishLog shellPublishLog) {
        shellPublishLogRepository.save(shellPublishLog);
    }

    public void save(List<ShellPublishLog> shellPublishLogs) {
        shellPublishLogRepository.saveAll(shellPublishLogs);
    }

    public void delete(Long shellPublishId) {
        shellPublishLogRepository.deleteByShellPublishId(shellPublishId);
    }
}
