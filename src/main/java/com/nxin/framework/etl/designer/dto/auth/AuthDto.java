package com.nxin.framework.etl.designer.dto.auth;

import com.nxin.framework.etl.designer.dto.CrudDto;
import lombok.Data;

import java.util.List;

@Data
public class AuthDto extends CrudDto {
    private String username;
    private String password;
    private String email;
    private String name;
    /**
     * 找回密码所依赖的验证码
     */
    private String code;
    private List<Long> resources;
}
