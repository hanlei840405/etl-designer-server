package com.nxin.framework.etl.designer.controller.analysis;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.converter.bean.analysis.LayoutConverter;
import com.nxin.framework.etl.designer.dto.CrudDto;
import com.nxin.framework.etl.designer.dto.analysis.LayoutDto;
import com.nxin.framework.etl.designer.entity.analysis.Layout;
import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.event.LayoutEvent;
import com.nxin.framework.etl.designer.service.analysis.LayoutService;
import com.nxin.framework.etl.designer.service.analysis.ModelService;
import com.nxin.framework.etl.designer.service.basic.UserService;
import com.nxin.framework.etl.designer.vo.analysis.LayoutVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class LayoutController {
    @Autowired
    private LayoutService layoutService;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelService modelService;
    @Autowired
    private ApplicationContext applicationContext;

    private BeanConverter<LayoutVo, Layout> reportConverter = new LayoutConverter();

    @PreAuthorize("hasAuthority('ROOT') or hasAuthority('LAYOUT')")
    @GetMapping("/layout/{id}")
    public ResponseEntity<LayoutVo> one(@PathVariable Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        List<String> resourceCodeList = loginUser.getUserPrivileges().stream().map(userPrivilege -> userPrivilege.getPrivilege().getResource().getCode()).collect(Collectors.toList());
        Layout layout = layoutService.one(id, loginUser.getTenant().getId());
        if (layout != null && resourceCodeList.contains(layout.getResourceCode())) {
            return ResponseEntity.ok(reportConverter.convert(layout, true));
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PreAuthorize("hasAuthority('ROOT') or hasAuthority('LAYOUT')")
    @PostMapping("/layouts")
    public ResponseEntity<List<LayoutVo>> layouts(@RequestBody CrudDto crudDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        List<String> resourceCodeList = loginUser.getUserPrivileges().stream().map(userPrivilege -> userPrivilege.getPrivilege().getResource().getCode()).collect(Collectors.toList());
        List<LayoutVo> layoutVoList = reportConverter.convert(layoutService.search(crudDto.getPayload(), resourceCodeList, loginUser.getTenant().getId()));
        return ResponseEntity.ok(layoutVoList);
    }

    @PostMapping("/layouts/show")
    public ResponseEntity<List<LayoutVo>> show(@RequestBody CrudDto crudDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        List<String> resourceCodeList = loginUser.getUserPrivileges().stream().map(userPrivilege -> userPrivilege.getPrivilege().getResource().getCode()).collect(Collectors.toList());
        List<LayoutVo> layoutVoList = reportConverter.convert(layoutService.show(crudDto.getPayload(), resourceCodeList, loginUser.getTenant().getId()));
        return ResponseEntity.ok(layoutVoList);
    }

    @PreAuthorize("hasAuthority('ROOT') or hasAuthority('LAYOUT')")
    @PostMapping("/layout")
    public ResponseEntity<LayoutVo> save(@RequestBody LayoutDto layoutDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        List<String> resourceCodeList = loginUser.getUserPrivileges().stream().map(userPrivilege -> userPrivilege.getPrivilege().getResource().getCode()).collect(Collectors.toList());
        Layout persisted;
        if (layoutDto.getId() != null) {
            persisted = layoutService.one(layoutDto.getId(), loginUser.getTenant().getId());
            if (persisted != null && resourceCodeList.contains(persisted.getResourceCode())) {
                BeanUtils.copyProperties(layoutDto, persisted, "id");
                persisted.setModifier(principal.getName());
                persisted = layoutService.save(persisted, loginUser.getTenant());
                return ResponseEntity.ok(reportConverter.convert(persisted, false));
            }
            return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
        }
        persisted = new Layout();
        BeanUtils.copyProperties(layoutDto, persisted, "id");
        persisted.setStatus(Constant.ACTIVE);
        persisted.setCreator(principal.getName());
        persisted = layoutService.save(persisted, loginUser.getTenant());
        LayoutEvent layoutEvent = new LayoutEvent(persisted);
        layoutEvent.setUser(loginUser);
        applicationContext.publishEvent(layoutEvent);
        return ResponseEntity.ok(reportConverter.convert(persisted, false));
    }

    @PreAuthorize("hasAuthority('ROOT') or hasAuthority('LAYOUT')")
    @DeleteMapping("/layout/{id}")
    public ResponseEntity<LayoutVo> delete(@PathVariable("id") Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        List<String> resourceCodeList = loginUser.getUserPrivileges().stream().map(userPrivilege -> userPrivilege.getPrivilege().getResource().getCode()).collect(Collectors.toList());
        Layout persisted = layoutService.one(id, loginUser.getTenant().getId());
        if (persisted != null && resourceCodeList.contains(persisted.getResourceCode())) {
            persisted.setModifier(principal.getName());
            persisted.setStatus(Constant.INACTIVE);
            persisted = layoutService.delete(persisted);
            return ResponseEntity.ok(reportConverter.convert(persisted, false));
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }
}
