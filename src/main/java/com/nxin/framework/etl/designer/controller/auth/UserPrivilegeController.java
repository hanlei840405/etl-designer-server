package com.nxin.framework.etl.designer.controller.auth;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.converter.bean.auth.ResourceConverter;
import com.nxin.framework.etl.designer.converter.bean.auth.UserPrivilegeConverter;
import com.nxin.framework.etl.designer.dto.auth.AuthDto;
import com.nxin.framework.etl.designer.entity.auth.Resource;
import com.nxin.framework.etl.designer.entity.auth.UserPrivilege;
import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.service.auth.PrivilegeService;
import com.nxin.framework.etl.designer.service.auth.ResourceService;
import com.nxin.framework.etl.designer.service.auth.UserPrivilegeService;
import com.nxin.framework.etl.designer.service.basic.UserService;
import com.nxin.framework.etl.designer.vo.auth.ResourceVo;
import com.nxin.framework.etl.designer.vo.auth.UserPrivilegeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PreAuthorize("hasAuthority('ROOT')")
@RestController
@RequestMapping
public class UserPrivilegeController {
    @Autowired
    private UserService userService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private UserPrivilegeService userPrivilegeService;

    private BeanConverter<UserPrivilegeVo, UserPrivilege> userPrivilegeConverter = new UserPrivilegeConverter();
    private BeanConverter<ResourceVo, Resource> resourceConverter = new ResourceConverter();

    @GetMapping("/privileges")
    public ResponseEntity<Map<String, List<ResourceVo>>> privileges(Principal principal) {
        User loginUser = userService.one(principal.getName());
        return ResponseEntity.ok(resourceConverter.convert(resourceService.all(loginUser.getTenant().getId())).stream().collect(Collectors.groupingBy(ResourceVo::getCategory, LinkedHashMap::new, Collectors.toList())));
    }

    @GetMapping("/privileges/{id}")
    public ResponseEntity<List<UserPrivilegeVo>> privileges(@PathVariable("id") Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        User targetUser = userService.one(id, loginUser.getTenant().getId());
        if (!targetUser.getTenant().getId().equals(loginUser.getTenant().getId())) {
            return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
        }
        List<UserPrivilege> grantedPrivileges = userPrivilegeService.search(targetUser.getId(), targetUser.getTenant().getId());
        return ResponseEntity.ok(userPrivilegeConverter.convert(grantedPrivileges));
    }

    @PostMapping("/grant")
    public ResponseEntity<List<UserPrivilegeVo>> grant(@RequestBody AuthDto authDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        User targetUser = userService.one(authDto.getUsername());
        if (!targetUser.getTenant().getId().equals(loginUser.getTenant().getId())) {
            return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
        }
        List<UserPrivilege> userPrivileges = userPrivilegeService.grant(authDto.getResources(), principal.getName(), targetUser);
        List<UserPrivilegeVo> userPrivilegeVos = userPrivilegeConverter.convert(userPrivileges);
        return ResponseEntity.ok(userPrivilegeVos);
    }
}
