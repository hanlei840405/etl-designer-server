package com.nxin.framework.etl.designer.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass
public class BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String status;
    @Column
    private Date createTime = new Date();
    @Column
    private String creator;
    @Column
    private Date modifyTime = new Date();
    @Column
    private String modifier;
    @Version
    private int version;
}
