package com.nxin.framework.etl.designer.controller.basic;

import com.nxin.framework.etl.designer.converter.bean.BeanConverter;
import com.nxin.framework.etl.designer.converter.bean.base.UserConverter;
import com.nxin.framework.etl.designer.dto.CrudDto;
import com.nxin.framework.etl.designer.dto.basic.UserDto;
import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.jwt.JwtUserDetailsService;
import com.nxin.framework.etl.designer.service.basic.UserService;
import com.nxin.framework.etl.designer.vo.PageVo;
import com.nxin.framework.etl.designer.vo.basic.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;

@PreAuthorize("hasAuthority('ROOT') or hasAuthority('SETTING')")
@RestController
@RequestMapping
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private BeanConverter<UserVo, User> userConverter = new UserConverter();

    @GetMapping("/user/{id}")
    public ResponseEntity<UserVo> one(@PathVariable("id") Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        User targetUser = userService.one(id, loginUser.getTenant().getId());
        if (targetUser != null && targetUser.getTenant().equals(loginUser.getTenant())) {
            return ResponseEntity.ok(userConverter.convert(targetUser, true));
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PostMapping("/users")
    public ResponseEntity<PageVo<UserVo>> users(@RequestBody CrudDto crudDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        PageVo<User> userPageVo = userService.search(loginUser.getTenant().getId(), crudDto.getPayload(), crudDto.getPageNo(), crudDto.getPageSize());
        PageVo<UserVo> userVoPageVo = new PageVo<>(userPageVo.getTotal(), userConverter.convert(userPageVo.getItems()));
        return ResponseEntity.ok(userVoPageVo);
    }

    @PostMapping("/user")
    public ResponseEntity<UserVo> save(@RequestBody UserDto userDto, Principal principal) {
        User loginUser = userService.one(principal.getName());
        User persist;
        if (userDto.getId() != null) {
            persist = userService.one(userDto.getId(), loginUser.getTenant().getId());
            if (persist != null && persist.getTenant().equals(loginUser.getTenant())) {
                persist.setName(userDto.getName());
                persist.setEmail(userDto.getEmail());
                persist.setModifier(principal.getName());
                persist = userService.modify(persist);
                return ResponseEntity.ok(userConverter.convert(persist, false));
            }
            return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
        } else {
            persist = userService.one(userDto.getEmail());
            if (persist != null) {
                return ResponseEntity.status(Constant.EXCEPTION_EMAIL_EXISTED).build();
            }
            persist = new User();
            persist.setName(userDto.getName());
            persist.setEmail(userDto.getEmail());
            persist.setModifier(principal.getName());
            persist.setStatus(Constant.ACTIVE);
            persist.setCreator(principal.getName());
            persist.setTenant(loginUser.getTenant());
            persist.setPassword(bCryptPasswordEncoder.encode(Constant.DEFAULT_PWD));
            persist = userService.create(persist, loginUser.getTenant(), Collections.emptyList());
            return ResponseEntity.ok(userConverter.convert(persist, false));
        }
    }

    @PreAuthorize("hasAuthority('ROOT') or hasAuthority('PROBATION')")
    @PutMapping("/user/{id}")
    public ResponseEntity<UserVo> lock(@PathVariable("id") Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        User persist = userService.one(id, loginUser.getTenant().getId());
        if (persist != null && persist.getTenant().equals(loginUser.getTenant())) {
            persist.setModifier(principal.getName());
            persist = userService.lock(persist);
            return ResponseEntity.ok(userConverter.convert(persist, false));
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PreAuthorize("hasAuthority('ROOT') or hasAuthority('PROBATION')")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<UserVo> delete(@PathVariable("id") Long id, Principal principal) {
        User loginUser = userService.one(principal.getName());
        User persist = userService.one(id, loginUser.getTenant().getId());
        if (persist != null && persist.getTenant().equals(loginUser.getTenant())) {
            persist.setModifier(principal.getName());
            persist = userService.close(persist);
            return ResponseEntity.ok(userConverter.convert(persist, false));
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }
}
