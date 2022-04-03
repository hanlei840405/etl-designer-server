package com.nxin.framework.etl.designer.service.basic;

import com.nxin.framework.etl.designer.entity.basic.Tenant;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.repository.basic.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class TenantService {
    @Autowired
    private TenantRepository tenantRepository;

    public Tenant register(Tenant tenant) {
        tenant.setStatus(Constant.ACTIVE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, Constant.PROBATION_DAYS);
        tenant.setProbationEndDate(calendar.getTime());
        tenantRepository.save(tenant);
        return tenant;
    }

    public void expire() {
        Date date = new Date();
        List<Tenant> tenants = tenantRepository.findAllByProbationEndDateAfter(date);
        tenants.forEach(tenant -> tenant.setStatus(Constant.INACTIVE));
        tenantRepository.saveAll(tenants);
    }
}
