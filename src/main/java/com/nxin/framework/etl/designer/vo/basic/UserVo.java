package com.nxin.framework.etl.designer.vo.basic;

import com.nxin.framework.etl.designer.vo.BaseVo;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class UserVo extends BaseVo implements Serializable {

    private boolean master;
    private String name;
    private String mobile;
    private String email;
    private String gender;
    private String wechat;
    private String password;
    private Date birthDate;
    private List<ProjectVo> projects = new ArrayList<>(0);

}
