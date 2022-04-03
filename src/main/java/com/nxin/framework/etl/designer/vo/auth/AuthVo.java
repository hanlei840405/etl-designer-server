package com.nxin.framework.etl.designer.vo.auth;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
public class AuthVo implements Serializable {
    private String token;
    private String username;
    private String name;
    private String tenant;
    private List<String> authorities;
}
