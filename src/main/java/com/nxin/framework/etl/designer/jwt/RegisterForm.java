package com.nxin.framework.etl.designer.jwt;

import com.nxin.framework.etl.designer.dto.CrudDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterForm extends CrudDto {
    @NotBlank(message = "公司信息禁止为空")
    private String company;
    @NotBlank(message = "姓名信息禁止为空")
    private String name;
    @NotBlank(message = "邮箱信息禁止为空")
    private String email;
    @NotBlank(message = "密码信息禁止为空")
    private String password;
}
